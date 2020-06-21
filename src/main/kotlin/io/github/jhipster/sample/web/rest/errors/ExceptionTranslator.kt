package io.github.jhipster.sample.web.rest.errors

import io.github.jhipster.web.util.HeaderUtil
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.zalando.problem.DefaultProblem
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.spring.web.advice.ProblemHandling
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait
import org.zalando.problem.violations.ConstraintViolationProblem

private const val FIELD_ERRORS_KEY = "fieldErrors"
private const val MESSAGE_KEY = "message"
private const val PATH_KEY = "path"
private const val VIOLATIONS_KEY = "violations"

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
class ExceptionTranslator : ProblemHandling, SecurityAdviceTrait {

    @Value("\${jhipster.clientApp.name}")
    private val applicationName: String? = null

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    override fun process(entity: ResponseEntity<Problem>?, request: NativeWebRequest?): ResponseEntity<Problem>? {
        if (entity == null) {
            return null
        }
        val problem = entity.body
        if (!(problem is ConstraintViolationProblem || problem is DefaultProblem)) {
            return entity
        }
        val builder = Problem.builder()
            .withType(if (Problem.DEFAULT_TYPE == problem.type) DEFAULT_TYPE else problem.type)
            .withStatus(problem.status)
            .withTitle(problem.title)
            .with(PATH_KEY, request!!.getNativeRequest(HttpServletRequest::class.java)!!.requestURI)

        if (problem is ConstraintViolationProblem) {
            builder
                .with(VIOLATIONS_KEY, problem.violations)
                .with(MESSAGE_KEY, ERR_VALIDATION)
        } else {
            builder
                .withCause((problem as DefaultProblem).cause)
                .withDetail(problem.detail)
                .withInstance(problem.instance)
            problem.parameters.forEach { (key, value) -> builder.with(key, value) }
            if (!problem.parameters.containsKey(MESSAGE_KEY) && problem.status != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.status!!.statusCode)
            }
        }
        return ResponseEntity(builder.build(), entity.headers, entity.statusCode)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: NativeWebRequest
    ): ResponseEntity<Problem>? {
        val result = ex.bindingResult
        val fieldErrors = result.fieldErrors.map { FieldErrorVM(it.objectName.replaceFirst(Regex("DTO$"), ""), it.field, it.code) }

        val problem = Problem.builder()
            .withType(CONSTRAINT_VIOLATION_TYPE)
            .withTitle("Method argument not valid")
            .withStatus(defaultConstraintViolationStatus())
            .with(MESSAGE_KEY, ERR_VALIDATION)
            .with(FIELD_ERRORS_KEY, fieldErrors)
            .build()
        return create(ex, problem, request)
    }

    @ExceptionHandler
    fun handleEmailAlreadyUsedException(ex: io.github.jhipster.sample.service.EmailAlreadyUsedException, request: NativeWebRequest): ResponseEntity<Problem>? {
        val problem = EmailAlreadyUsedException()
        return create(problem, request, HeaderUtil.createFailureAlert(applicationName, true, problem.entityName, problem.errorKey, problem.message))
    }

    @ExceptionHandler
    fun handleUsernameAlreadyUsedException(ex: io.github.jhipster.sample.service.UsernameAlreadyUsedException, request: NativeWebRequest): ResponseEntity<Problem>? {
        val problem = LoginAlreadyUsedException()
        return create(problem, request, HeaderUtil.createFailureAlert(applicationName, true, problem.entityName, problem.errorKey, problem.message))
    }

    @ExceptionHandler
    fun handleInvalidPasswordException(ex: io.github.jhipster.sample.service.InvalidPasswordException, request: NativeWebRequest): ResponseEntity<Problem>? {
        return create(InvalidPasswordException(), request)
    }

    @ExceptionHandler
    fun handleBadRequestAlertException(
        ex: BadRequestAlertException,
        request: NativeWebRequest
    ): ResponseEntity<Problem>? =
        create(
            ex, request,
            HeaderUtil.createFailureAlert(applicationName, true, ex.entityName, ex.errorKey, ex.message)
        )

    @ExceptionHandler
    fun handleConcurrencyFailure(ex: ConcurrencyFailureException, request: NativeWebRequest): ResponseEntity<Problem>? {
        val problem = Problem.builder()
            .withStatus(Status.CONFLICT)
            .with(MESSAGE_KEY, ERR_CONCURRENCY_FAILURE)
            .build()
        return create(ex, problem, request)
    }
}
