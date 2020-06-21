package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.domain.Label
import io.github.jhipster.sample.service.LabelService
import io.github.jhipster.sample.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "testRootLabel"
/**
 * REST controller for managing [io.github.jhipster.sample.domain.Label].
 */
@RestController
@RequestMapping("/api")
class LabelResource(
    private val labelService: LabelService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /labels` : Create a new label.
     *
     * @param label the label to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new label, or with status `400 (Bad Request)` if the label has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/labels")
    fun createLabel(@Valid @RequestBody label: Label): ResponseEntity<Label> {
        log.debug("REST request to save Label : {}", label)
        if (label.id != null) {
            throw BadRequestAlertException(
                "A new label cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = labelService.save(label)
        return ResponseEntity.created(URI("/api/labels/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /labels` : Updates an existing label.
     *
     * @param label the label to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated label,
     * or with status `400 (Bad Request)` if the label is not valid,
     * or with status `500 (Internal Server Error)` if the label couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/labels")
    fun updateLabel(@Valid @RequestBody label: Label): ResponseEntity<Label> {
        log.debug("REST request to update Label : {}", label)
        if (label.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = labelService.save(label)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     label.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /labels` : get all the labels.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of labels in body.
     */
    @GetMapping("/labels")
    fun getAllLabels(pageable: Pageable): ResponseEntity<List<Label>> {
        log.debug("REST request to get a page of Labels")
        val page = labelService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /labels/:id` : get the "id" label.
     *
     * @param id the id of the label to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the label, or with status `404 (Not Found)`.
     */
    @GetMapping("/labels/{id}")
    fun getLabel(@PathVariable id: Long): ResponseEntity<Label> {
        log.debug("REST request to get Label : {}", id)
        val label = labelService.findOne(id)
        return ResponseUtil.wrapOrNotFound(label)
    }
    /**
     *  `DELETE  /labels/:id` : delete the "id" label.
     *
     * @param id the id of the label to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/labels/{id}")
    fun deleteLabel(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Label : {}", id)

        labelService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
