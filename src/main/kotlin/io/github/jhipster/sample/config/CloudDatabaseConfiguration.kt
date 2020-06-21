package io.github.jhipster.sample.config

import io.github.jhipster.config.JHipsterConstants
import javax.sql.DataSource
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.config.java.AbstractCloudConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

private const val CLOUD_CONFIGURATION_HIKARI_PREFIX = "spring.datasource.hikari"

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_CLOUD)
class CloudDatabaseConfiguration : AbstractCloudConfig() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    @ConfigurationProperties(CLOUD_CONFIGURATION_HIKARI_PREFIX)
    fun dataSource(): DataSource {
        log.info("Configuring JDBC datasource from a cloud provider")
        return connectionFactory().dataSource()
    }
}
