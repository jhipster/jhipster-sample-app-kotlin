package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.domain.Operation
import io.github.jhipster.sample.repository.OperationRepository
import io.github.jhipster.sample.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "testRootOperation"
/**
 * REST controller for managing [io.github.jhipster.sample.domain.Operation].
 */
@RestController
@RequestMapping("/api")
@Transactional
class OperationResource(
    private val operationRepository: OperationRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /operations` : Create a new operation.
     *
     * @param operation the operation to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new operation, or with status `400 (Bad Request)` if the operation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/operations")
    fun createOperation(@Valid @RequestBody operation: Operation): ResponseEntity<Operation> {
        log.debug("REST request to save Operation : {}", operation)
        if (operation.id != null) {
            throw BadRequestAlertException(
                "A new operation cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = operationRepository.save(operation)
        return ResponseEntity.created(URI("/api/operations/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /operations` : Updates an existing operation.
     *
     * @param operation the operation to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated operation,
     * or with status `400 (Bad Request)` if the operation is not valid,
     * or with status `500 (Internal Server Error)` if the operation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/operations")
    fun updateOperation(@Valid @RequestBody operation: Operation): ResponseEntity<Operation> {
        log.debug("REST request to update Operation : {}", operation)
        if (operation.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = operationRepository.save(operation)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     operation.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /operations` : get all the operations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of operations in body.
     */
    @GetMapping("/operations")
    fun getAllOperations(pageable: Pageable, @RequestParam(required = false, defaultValue = "false") eagerload: Boolean): ResponseEntity<List<Operation>> {
        log.debug("REST request to get a page of Operations")
        val page: Page<Operation> = if (eagerload) {
            operationRepository.findAllWithEagerRelationships(pageable)
        } else {
            operationRepository.findAll(pageable)
        }
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /operations/:id` : get the "id" operation.
     *
     * @param id the id of the operation to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the operation, or with status `404 (Not Found)`.
     */
    @GetMapping("/operations/{id}")
    fun getOperation(@PathVariable id: Long): ResponseEntity<Operation> {
        log.debug("REST request to get Operation : {}", id)
        val operation = operationRepository.findOneWithEagerRelationships(id)
        return ResponseUtil.wrapOrNotFound(operation)
    }
    /**
     *  `DELETE  /operations/:id` : delete the "id" operation.
     *
     * @param id the id of the operation to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/operations/{id}")
    fun deleteOperation(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Operation : {}", id)

        operationRepository.deleteById(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
