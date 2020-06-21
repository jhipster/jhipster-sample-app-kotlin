package io.github.jhipster.sample.config

import ch.qos.logback.classic.LoggerContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.config.logging.LoggingUtils.addContextListener
import io.github.jhipster.config.logging.LoggingUtils.addJsonConsoleAppender
import io.github.jhipster.config.logging.LoggingUtils.addLogstashTcpSocketAppender
import io.github.jhipster.config.logging.LoggingUtils.setMetricsMarkerLogbackFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

/*
 * Configures the console and Logstash log appenders from the app properties.
 */
@Configuration
class LoggingConfiguration(
    @Value("\${spring.application.name}") appName: String,
    @Value("\${server.port}") serverPort: String,
    jHipsterProperties: JHipsterProperties,
    mapper: ObjectMapper
) {
    init {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext

        val map = mutableMapOf<String, String?>()
        map["app_name"] = appName
        map["app_port"] = serverPort
        val customFields = mapper.writeValueAsString(map)

        val loggingProperties = jHipsterProperties.logging
        val logstashProperties = loggingProperties.logstash

        if (loggingProperties.isUseJsonFormat) {
            addJsonConsoleAppender(context, customFields)
        }
        if (logstashProperties.isEnabled) {
            addLogstashTcpSocketAppender(context, customFields, logstashProperties)
        }
        if (loggingProperties.isUseJsonFormat || logstashProperties.isEnabled) {
            addContextListener(context, customFields, loggingProperties)
        }
        if (jHipsterProperties.metrics.logs.isEnabled) {
            setMetricsMarkerLogbackFilter(context, loggingProperties.isUseJsonFormat)
        }
    }
}
