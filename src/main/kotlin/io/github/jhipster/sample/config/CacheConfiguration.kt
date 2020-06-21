package io.github.jhipster.sample.config

import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.config.cache.PrefixedKeyGenerator
import java.time.Duration
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.jsr107.Eh107Configuration
import org.hibernate.cache.jcache.ConfigSettings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfiguration(
    @Autowired val gitProperties: GitProperties?,
    @Autowired val buildProperties: BuildProperties?,
    jHipsterProperties: JHipsterProperties
) {

    private val jcacheConfiguration: javax.cache.configuration.Configuration<Any, Any>

    init {
        val ehcache = jHipsterProperties.cache.ehcache

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Any::class.java, Any::class.java,
                ResourcePoolsBuilder.heap(ehcache.maxEntries)
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.timeToLiveSeconds.toLong())))
                .build()
        )
    }

    @Bean
    fun hibernatePropertiesCustomizer(cacheManager: javax.cache.CacheManager) = HibernatePropertiesCustomizer {
        hibernateProperties -> hibernateProperties[ConfigSettings.CACHE_MANAGER] = cacheManager
    }

    @Bean
    fun cacheManagerCustomizer(): JCacheManagerCustomizer {
        return JCacheManagerCustomizer { cm ->
            createCache(cm, io.github.jhipster.sample.repository.UserRepository.USERS_BY_LOGIN_CACHE)
            createCache(cm, io.github.jhipster.sample.repository.UserRepository.USERS_BY_EMAIL_CACHE)
            createCache(cm, io.github.jhipster.sample.domain.User::class.java.name)
            createCache(cm, io.github.jhipster.sample.domain.Authority::class.java.name)
            createCache(cm, io.github.jhipster.sample.domain.User::class.java.name + ".authorities")
            createCache(cm, io.github.jhipster.sample.domain.BankAccount::class.java.name)
            createCache(cm, io.github.jhipster.sample.domain.BankAccount::class.java.name + ".operations")
            createCache(cm, io.github.jhipster.sample.domain.Label::class.java.name)
            createCache(cm, io.github.jhipster.sample.domain.Label::class.java.name + ".operations")
            createCache(cm, io.github.jhipster.sample.domain.Operation::class.java.name)
            createCache(cm, io.github.jhipster.sample.domain.Operation::class.java.name + ".labels")
            // jhipster-needle-ehcache-add-entry
        }
    }

    private fun createCache(cm: javax.cache.CacheManager, cacheName: String) {
        val cache: javax.cache.Cache<Any, Any>? = cm.getCache(cacheName)
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration)
        }
    }

        @Bean
        fun keyGenerator() = PrefixedKeyGenerator(gitProperties, buildProperties)
}
