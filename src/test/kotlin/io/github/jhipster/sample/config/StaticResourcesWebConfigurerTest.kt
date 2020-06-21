package io.github.jhipster.sample.config

import com.nhaarman.mockitokotlin2.anyOrNull
import io.github.jhipster.config.JHipsterDefaults
import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.sample.config.StaticResourcesWebConfiguration.Companion.RESOURCE_LOCATIONS
import io.github.jhipster.sample.config.StaticResourcesWebConfiguration.Companion.RESOURCE_PATHS
import java.util.concurrent.TimeUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.CacheControl
import org.springframework.mock.web.MockServletContext
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry

class StaticResourcesWebConfigurerTest {
    private lateinit var staticResourcesWebConfiguration: StaticResourcesWebConfiguration
    private lateinit var resourceHandlerRegistry: ResourceHandlerRegistry
    private lateinit var servletContext: MockServletContext
    private lateinit var applicationContext: WebApplicationContext
    private lateinit var props: JHipsterProperties

    @BeforeEach
    fun setUp() {
        servletContext = spy(MockServletContext())
        applicationContext = mock(WebApplicationContext::class.java)
        resourceHandlerRegistry = spy(ResourceHandlerRegistry(applicationContext, servletContext))
        props = JHipsterProperties()
        staticResourcesWebConfiguration = spy(StaticResourcesWebConfiguration(props))
    }

    @Test
    fun shouldAppendResourceHandlerAndInitiliazeIt() {

        staticResourcesWebConfiguration.addResourceHandlers(resourceHandlerRegistry)

        verify(resourceHandlerRegistry, times(1))
            .addResourceHandler(*RESOURCE_PATHS)
        verify(staticResourcesWebConfiguration, times(1))
            .initializeResourceHandler(anyOrNull())
        RESOURCE_PATHS.forEach {
            assertThat(resourceHandlerRegistry.hasMappingForPattern(it)).isTrue()
        }
    }

    @Test
    fun shouldInitializeResourceHandlerWithCacheControlAndLocations() {
        val ccExpected = CacheControl.maxAge(5, TimeUnit.DAYS).cachePublic()
        `when`(staticResourcesWebConfiguration.getCacheControl()).thenReturn(ccExpected)
        val resourceHandlerRegistration = spy(ResourceHandlerRegistration(*RESOURCE_PATHS))

        staticResourcesWebConfiguration.initializeResourceHandler(resourceHandlerRegistration)

        verify(staticResourcesWebConfiguration, times(1)).getCacheControl()
        verify(resourceHandlerRegistration, times(1)).setCacheControl(ccExpected)
        verify(resourceHandlerRegistration, times(1)).addResourceLocations(*RESOURCE_LOCATIONS)
    }

    @Test
    fun shoudCreateCacheControlBasedOnJhipsterDefaultProperties() {
        val cacheExpected = CacheControl.maxAge(JHipsterDefaults.Http.Cache.timeToLiveInDays.toLong(), TimeUnit.DAYS).cachePublic()
        assertThat(staticResourcesWebConfiguration.getCacheControl())
            .extracting(CacheControl::getHeaderValue)
            .isEqualTo(cacheExpected.headerValue)
    }

    @Test
    fun shoudCreateCacheControlWithSpecificConfigurationInProperties() {
        props.http.cache.timeToLiveInDays = MAX_AGE_TEST
        val cacheExpected = CacheControl.maxAge(MAX_AGE_TEST.toLong(), TimeUnit.DAYS).cachePublic()
        assertThat(staticResourcesWebConfiguration.getCacheControl())
            .extracting(CacheControl::getHeaderValue)
            .isEqualTo(cacheExpected.headerValue)
    }

    companion object {
        const val MAX_AGE_TEST = 5
    }
}
