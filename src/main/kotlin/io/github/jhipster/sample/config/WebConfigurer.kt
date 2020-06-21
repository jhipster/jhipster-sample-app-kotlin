package io.github.jhipster.sample.config

import io.github.jhipster.config.JHipsterConstants
import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.config.h2.H2ConfigurationHelper
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import javax.servlet.ServletContext
import javax.servlet.ServletException
import org.slf4j.LoggerFactory
import org.springframework.boot.web.server.MimeMappings
import org.springframework.boot.web.server.WebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.MediaType
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
class WebConfigurer(
    private val env: Environment,
    private val jHipsterProperties: JHipsterProperties
) : ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {

    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(ServletException::class)
    override fun onStartup(servletContext: ServletContext) {
        if (env.activeProfiles.isNotEmpty()) {
            log.info("Web application configuration, using profiles: {}", *env.activeProfiles as Array<*>)
        }

        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            initH2Console(servletContext)
        }
        log.info("Web application fully configured")
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    override fun customize(server: WebServerFactory) {
        setMimeMappings(server)
        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
        setLocationForStaticAssets(server)
    }

    private fun setMimeMappings(server: WebServerFactory) {
        if (server is ConfigurableServletWebServerFactory) {
            val mappings = MimeMappings(MimeMappings.DEFAULT)
            // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
            mappings.add("html", "${MediaType.TEXT_HTML_VALUE};charset=${StandardCharsets.UTF_8.name().toLowerCase()}")
            // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
            mappings.add("json", "${MediaType.TEXT_HTML_VALUE};charset=${StandardCharsets.UTF_8.name().toLowerCase()}")
            server.setMimeMappings(mappings)
        }
    }

    private fun setLocationForStaticAssets(server: WebServerFactory) {
        if (server is ConfigurableServletWebServerFactory) {
            val prefixPath = resolvePathPrefix()
            val root = File(prefixPath + "target/classes/static/")
            if (root.exists() && root.isDirectory) {
                server.setDocumentRoot(root)
            }
        }
    }

    /**
     * Resolve path prefix to static resources.
     */
    private fun resolvePathPrefix(): String {
        val fullExecutablePath = try {
            decode(this.javaClass.getResource("").path, StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            /* try without decoding if this ever happens */
            this.javaClass.getResource("").path
        }

        val rootPath = Paths.get(".").toUri().normalize().path
        val extractedPath = fullExecutablePath.replace(rootPath, "")
        val extractionEndIndex = extractedPath.indexOf("target/")
        if (extractionEndIndex <= 0) {
            return ""
        }
        return extractedPath.substring(0, extractionEndIndex)
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = jHipsterProperties.cors
        if (config.allowedOrigins != null && config.allowedOrigins!!.isNotEmpty()) {
            log.debug("Registering CORS filter")
            source.apply {
                registerCorsConfiguration("/api/**", config)
                registerCorsConfiguration("/management/**", config)
                registerCorsConfiguration("/v2/api-docs", config)
            }
        }
        return CorsFilter(source)
    }

    /**
     * Initializes H2 console.
     */
    private fun initH2Console(servletContext: ServletContext) {
        log.debug("Initialize H2 console")
        H2ConfigurationHelper.initH2Console(servletContext)
    }
}
