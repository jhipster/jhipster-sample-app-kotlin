package io.github.jhipster.sample.config

import io.github.jhipster.async.ExceptionHandlingAsyncTaskExecutor
import java.util.concurrent.Executor
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
@EnableScheduling
class AsyncConfiguration(private val taskExecutionProperties: TaskExecutionProperties) : AsyncConfigurer {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean(name = ["taskExecutor"])
    override fun getAsyncExecutor(): Executor {
        log.debug("Creating Async Task Executor")
        val executor = ThreadPoolTaskExecutor().apply {
            corePoolSize = taskExecutionProperties.pool.coreSize
            maxPoolSize = taskExecutionProperties.pool.maxSize
            setQueueCapacity(taskExecutionProperties.pool.queueCapacity)
            threadNamePrefix = taskExecutionProperties.threadNamePrefix
        }
        return ExceptionHandlingAsyncTaskExecutor(executor)
    }

    override fun getAsyncUncaughtExceptionHandler() = SimpleAsyncUncaughtExceptionHandler()
}
