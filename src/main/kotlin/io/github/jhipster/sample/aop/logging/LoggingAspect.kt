package io.github.jhipster.sample.aop.logging

import io.github.jhipster.config.JHipsterConstants
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * By default, it only runs with the "dev" profile.
 */
@Suppress("unused")
@Aspect
open class LoggingAspect(private val env: Environment) {

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut(
        "within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    fun springBeanPointcut() =
        Unit // Method is empty as this is just a Pointcut, the implementations are in the advices.

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut(
        "within(io.github.jhipster.sample.repository..*)" +
            " || within(io.github.jhipster.sample.service..*)" +
            " || within(io.github.jhipster.sample.web.rest..*)"
    )
    fun applicationPackagePointcut() =
        Unit // Method is empty as this is just a Pointcut, the implementations are in the advices.

    /**
    * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
    *
    * @param joinPoint join point we want the logger for.
    * @return {@link Logger} associated to the given {@link JoinPoint}.
    */
    private fun logger(joinPoint: JoinPoint) = LoggerFactory.getLogger(joinPoint.signature.declaringTypeName)

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice.
     * @param e exception.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    fun logAfterThrowing(joinPoint: JoinPoint, e: Throwable) {
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            logger(joinPoint).error(
                "Exception in {}() with cause = \'{}\' and exception = \'{}\'",
                joinPoint.signature.name,
                if (e.cause != null) e.cause else "NULL",
                e.message, e
            )
        } else {
            logger(joinPoint).error(
                "Exception in {}() with cause = {}", joinPoint.signature.name,
                if (e.cause != null) e.cause else "NULL"
            )
        }
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws [IllegalArgumentException].
     */
    @Around(value = "applicationPackagePointcut() && springBeanPointcut()")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        val log = logger(joinPoint)
        if (log.isDebugEnabled) {
            log.debug(
                "Enter: {}() with argument[s] = {}",
                joinPoint.signature.name, joinPoint.args.joinToString()
            )
        }
        try {
            val result = joinPoint.proceed()
            if (log.isDebugEnabled) {
                log.debug(
                    "Exit: {}() with result = {}", joinPoint.signature.name, result
                )
            }
            return result
        } catch (e: IllegalArgumentException) {
            log.error(
                "Illegal argument: {} in {}()", joinPoint.args.joinToString(),
                joinPoint.signature.name
            )

            throw e
        }
    }
}
