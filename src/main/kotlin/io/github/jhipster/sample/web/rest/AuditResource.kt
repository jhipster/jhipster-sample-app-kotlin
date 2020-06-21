package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.service.AuditEventService
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.time.LocalDate
import java.time.ZoneId
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * REST controller for getting the `AuditEvent`s.
 */
@RestController
@RequestMapping("/management/audits")
class AuditResource(private val auditEventService: AuditEventService) {

    /**
     * `GET /audits` : get a page of `AuditEvent`s.
     *
     * @param pageable the pagination information.
     * @return the `ResponseEntity` with status `200 (OK)` and the list of `AuditEvent`s in body.
     */
    @GetMapping
    fun getAll(pageable: Pageable): ResponseEntity<List<AuditEvent>> {
        val page = auditEventService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity(page.content, headers, HttpStatus.OK)
    }

    /**
     * `GET  /audits` : get a page of `AuditEvent`s between the `fromDate` and `toDate`.
     *
     * @param fromDate the start of the time period of `AuditEvent`s to get.
     * @param toDate the end of the time period of `AuditEvent`s to get.
     * @param pageable the pagination information.
     * @return the `ResponseEntity` with status `200 (OK)` and the list of `AuditEvent`s in body.
     */
    @GetMapping(params = ["fromDate", "toDate"])
    fun getByDates(
        @RequestParam(value = "fromDate") fromDate: LocalDate,
        @RequestParam(value = "toDate") toDate: LocalDate,
        pageable: Pageable
    ): ResponseEntity<List<AuditEvent>> {

        val from = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val to = toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant()

        val page = auditEventService.findByDates(from, to, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity(page.content, headers, HttpStatus.OK)
    }

    /**
     * `GET  /audits/:id` : get an `AuditEvent` by id.
     *
     * @param id the id of the entity to get.
     * @return the `ResponseEntity` with status `200 (OK)` and the AuditEvent in body, or status `404 (Not Found)`.
     */
    @GetMapping("/{id:.+}")
    fun get(@PathVariable id: Long?): ResponseEntity<AuditEvent> =
        ResponseUtil.wrapOrNotFound(auditEventService.find(id!!))
}
