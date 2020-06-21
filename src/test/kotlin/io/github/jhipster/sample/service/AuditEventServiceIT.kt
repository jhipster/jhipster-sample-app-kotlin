
package io.github.jhipster.sample.service

import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.domain.PersistentAuditEvent
import io.github.jhipster.sample.repository.PersistenceAuditEventRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

/**
 * Integration tests for {@link AuditEventService}.
 */
@SpringBootTest(classes = [JhipsterApp::class])
@Transactional
class AuditEventServiceIT {
    @Autowired
    private lateinit var auditEventService: AuditEventService

    @Autowired
    private lateinit var persistenceAuditEventRepository: PersistenceAuditEventRepository

    @Autowired
    private lateinit var jHipsterProperties: JHipsterProperties

    private lateinit var auditEventOld: PersistentAuditEvent

    private lateinit var auditEventWithinRetention: PersistentAuditEvent

    private lateinit var auditEventNew: PersistentAuditEvent

    @BeforeEach
    fun init() {
        auditEventOld = PersistentAuditEvent()
        auditEventOld.auditEventDate = Instant.now().minus((jHipsterProperties.auditEvents.retentionPeriod + 1).toLong(), ChronoUnit.DAYS)
        auditEventOld.principal = "test-user-old"
        auditEventOld.auditEventType = "test-type"

        auditEventWithinRetention = PersistentAuditEvent()
        auditEventWithinRetention.auditEventDate = Instant.now().minus((jHipsterProperties.auditEvents.retentionPeriod - 1).toLong(), ChronoUnit.DAYS)
        auditEventWithinRetention.principal = "test-user-retention"
        auditEventWithinRetention.auditEventType = "test-type"

        auditEventNew = PersistentAuditEvent()
        auditEventNew.auditEventDate = Instant.now()
        auditEventNew.principal = "test-user-new"
        auditEventNew.auditEventType = "test-type"
    }

    @Test
    @Transactional
    fun verifyOldAuditEventsAreDeleted() {
        persistenceAuditEventRepository.deleteAll()
        persistenceAuditEventRepository.save(auditEventOld)
        persistenceAuditEventRepository.save(auditEventWithinRetention)
        persistenceAuditEventRepository.save(auditEventNew)
        persistenceAuditEventRepository.flush()
        auditEventService.removeOldAuditEvents()
        persistenceAuditEventRepository.flush()

        assertThat(persistenceAuditEventRepository.findAll().size).isEqualTo(2)
        assertThat(persistenceAuditEventRepository.findByPrincipal("test-user-old")).isEmpty()
        assertThat(persistenceAuditEventRepository.findByPrincipal("test-user-retention")).isNotEmpty()
        assertThat(persistenceAuditEventRepository.findByPrincipal("test-user-new")).isNotEmpty()
    }
}
