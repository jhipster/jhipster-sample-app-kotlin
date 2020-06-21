package io.github.jhipster.sample.service.impl

import io.github.jhipster.sample.domain.BankAccount
import io.github.jhipster.sample.repository.BankAccountRepository
import io.github.jhipster.sample.service.BankAccountService
import io.github.jhipster.sample.service.dto.BankAccountDTO
import io.github.jhipster.sample.service.mapper.BankAccountMapper
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [BankAccount].
 */
@Service
@Transactional
class BankAccountServiceImpl(
    private val bankAccountRepository: BankAccountRepository,
    private val bankAccountMapper: BankAccountMapper
) : BankAccountService {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a bankAccount.
     *
     * @param bankAccountDTO the entity to save.
     * @return the persisted entity.
     */
    override fun save(bankAccountDTO: BankAccountDTO): BankAccountDTO {
        log.debug("Request to save BankAccount : {}", bankAccountDTO)

        var bankAccount = bankAccountMapper.toEntity(bankAccountDTO)
        bankAccount = bankAccountRepository.save(bankAccount)
        return bankAccountMapper.toDto(bankAccount)
    }

    /**
     * Get all the bankAccounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    override fun findAll(): MutableList<BankAccountDTO> {
        log.debug("Request to get all BankAccounts")
        return bankAccountRepository.findAll()
            .mapTo(mutableListOf(), bankAccountMapper::toDto)
    }

    /**
     * Get one bankAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<BankAccountDTO> {
        log.debug("Request to get BankAccount : {}", id)
        return bankAccountRepository.findById(id)
            .map(bankAccountMapper::toDto)
    }

    /**
     * Delete the bankAccount by id.
     *
     * @param id the id of the entity.
     */
    override fun delete(id: Long) {
        log.debug("Request to delete BankAccount : {}", id)

        bankAccountRepository.deleteById(id)
    }
}
