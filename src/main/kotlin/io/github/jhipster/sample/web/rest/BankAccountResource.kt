package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.service.BankAccountQueryService
import io.github.jhipster.sample.service.BankAccountService
import io.github.jhipster.sample.service.dto.BankAccountCriteria
import io.github.jhipster.sample.service.dto.BankAccountDTO
import io.github.jhipster.sample.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "testRootBankAccount"
/**
 * REST controller for managing [io.github.jhipster.sample.domain.BankAccount].
 */
@RestController
@RequestMapping("/api")
class BankAccountResource(
    private val bankAccountService: BankAccountService,
    private val bankAccountQueryService: BankAccountQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /bank-accounts` : Create a new bankAccount.
     *
     * @param bankAccountDTO the bankAccountDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new bankAccountDTO, or with status `400 (Bad Request)` if the bankAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bank-accounts")
    fun createBankAccount(@Valid @RequestBody bankAccountDTO: BankAccountDTO): ResponseEntity<BankAccountDTO> {
        log.debug("REST request to save BankAccount : {}", bankAccountDTO)
        if (bankAccountDTO.id != null) {
            throw BadRequestAlertException(
                "A new bankAccount cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = bankAccountService.save(bankAccountDTO)
        return ResponseEntity.created(URI("/api/bank-accounts/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /bank-accounts` : Updates an existing bankAccount.
     *
     * @param bankAccountDTO the bankAccountDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated bankAccountDTO,
     * or with status `400 (Bad Request)` if the bankAccountDTO is not valid,
     * or with status `500 (Internal Server Error)` if the bankAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bank-accounts")
    fun updateBankAccount(@Valid @RequestBody bankAccountDTO: BankAccountDTO): ResponseEntity<BankAccountDTO> {
        log.debug("REST request to update BankAccount : {}", bankAccountDTO)
        if (bankAccountDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = bankAccountService.save(bankAccountDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     bankAccountDTO.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /bank-accounts` : get all the bankAccounts.
     *

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of bankAccounts in body.
     */
    @GetMapping("/bank-accounts") fun getAllBankAccounts(
        criteria: BankAccountCriteria
    ): ResponseEntity<MutableList<BankAccountDTO>> {
        log.debug("REST request to get BankAccounts by criteria: {}", criteria)
        val entityList = bankAccountQueryService.findByCriteria(criteria)
        return ResponseEntity.ok().body(entityList)
    }

    /**
     * `GET  /bank-accounts/count}` : count all the bankAccounts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/bank-accounts/count")
    fun countBankAccounts(criteria: BankAccountCriteria): ResponseEntity<Long> {
        log.debug("REST request to count BankAccounts by criteria: {}", criteria)
        return ResponseEntity.ok().body(bankAccountQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /bank-accounts/:id` : get the "id" bankAccount.
     *
     * @param id the id of the bankAccountDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the bankAccountDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/bank-accounts/{id}")
    fun getBankAccount(@PathVariable id: Long): ResponseEntity<BankAccountDTO> {
        log.debug("REST request to get BankAccount : {}", id)
        val bankAccountDTO = bankAccountService.findOne(id)
        return ResponseUtil.wrapOrNotFound(bankAccountDTO)
    }
    /**
     *  `DELETE  /bank-accounts/:id` : delete the "id" bankAccount.
     *
     * @param id the id of the bankAccountDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/bank-accounts/{id}")
    fun deleteBankAccount(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete BankAccount : {}", id)

        bankAccountService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
