package io.github.jhipster.sample.config.audit

import io.github.jhipster.sample.domain.PersistentAuditEvent
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component

@Component
class AuditEventConverter {

    /**
     * Convert a list of [PersistentAuditEvent] to a list of [AuditEvent].
     *
     * @param persistentAuditEvents the list to convert.
     * @return the converted list.
     */
    fun convertToAuditEvent(persistentAuditEvents: Iterable<PersistentAuditEvent>?) =
        when (persistentAuditEvents) {
            null -> mutableListOf()
            else -> persistentAuditEvents.asSequence().mapNotNull { convertToAuditEvent(it) }.toMutableList()
        }

    /**
     * Convert a [PersistentAuditEvent] to an [AuditEvent].
     *
     * @param persistentAuditEvent the event to convert.
     * @return the converted list.
     */
    fun convertToAuditEvent(persistentAuditEvent: PersistentAuditEvent?): AuditEvent? =
        when (persistentAuditEvent) {
            null -> null
            else -> AuditEvent(
                persistentAuditEvent.auditEventDate, persistentAuditEvent.principal,
                persistentAuditEvent.auditEventType, convertDataToObjects(persistentAuditEvent.data)
            )
        }

    /**
     * Internal conversion. This is needed to support the current SpringBoot actuator[ AuditEventRepository] interface.
     *
     * @param data the data to convert.
     * @return a map of [String], [Any].
     */
    fun convertDataToObjects(data: MutableMap<String, String?>?): MutableMap<String, Any?> =
        data?.mapValuesTo(mutableMapOf()) { it.value } ?: mutableMapOf()

    /**
     * Internal conversion. This method will allow to save additional data.
     * By default, it will save the object as string.
     *
     * @param data the data to convert.
     * @return a map of [String], [String].
     */
    fun convertDataToStrings(data: Map<String, Any?>?): Map<String, String?> {
        val results = mutableMapOf<String, String?>()
        data?.forEach { (key, value) ->
            // Extract the data that will be saved.
            if (value is WebAuthenticationDetails) {
                results["remoteAddress"] = value.remoteAddress
                results["sessionId"] = value.sessionId
            } else {
                results[key] = value?.toString()
            }
        }
        return results
    }
}
