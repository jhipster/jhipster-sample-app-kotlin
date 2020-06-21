package io.github.jhipster.sample.service
import io.github.jhipster.sample.service.dto.BankAccountDTO
import java.util.Optional

/**
 * Service Interface for managing [io.github.jhipster.sample.domain.BankAccount].
 */
interface BankAccountService {

    /**
     * Save a bankAccount.
     *
     * @param bankAccountDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(bankAccountDTO: BankAccountDTO): BankAccountDTO

    /**
     * Get all the bankAccounts.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<BankAccountDTO>

    /**
     * Get the "id" bankAccount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<BankAccountDTO>

    /**
     * Delete the "id" bankAccount.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
