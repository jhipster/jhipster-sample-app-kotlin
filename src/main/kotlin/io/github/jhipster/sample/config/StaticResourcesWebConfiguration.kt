package io.github.jhipster.sample.config

import io.github.jhipster.config.JHipsterConstants
import io.github.jhipster.config.JHipsterProperties
import java.util.concurrent.TimeUnit
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
class StaticResourcesWebConfiguration(private val jHipsterProperties: JHipsterProperties) : WebMvcConfigurer {

    companion object {
        val RESOURCE_LOCATIONS = arrayOf("classpath:/static/app/", "classpath:/static/content/", "classpath:/static/i18n/")
        val RESOURCE_PATHS = arrayOf("/app/*", "/content/*", "/i18n/*")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val resourceHandlerRegistration = appendResourceHandler(registry)
        initializeResourceHandler(resourceHandlerRegistration)
    }

    fun appendResourceHandler(registry: ResourceHandlerRegistry) = registry.addResourceHandler(*RESOURCE_PATHS)

    fun initializeResourceHandler(resourceHandlerRegistration: ResourceHandlerRegistration) {
        resourceHandlerRegistration.addResourceLocations(*RESOURCE_LOCATIONS).setCacheControl(getCacheControl())
    }

    fun getCacheControl() = CacheControl.maxAge(getJHipsterHttpCacheProperty().toLong(), TimeUnit.DAYS).cachePublic()

    private fun getJHipsterHttpCacheProperty() = jHipsterProperties.http.cache.timeToLiveInDays
}
