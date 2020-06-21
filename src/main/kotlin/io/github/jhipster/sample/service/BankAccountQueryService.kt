package io.github.jhipster.sample.service

import io.github.jhipster.sample.domain.BankAccount
import io.github.jhipster.sample.domain.BankAccount_
import io.github.jhipster.sample.domain.Operation_
import io.github.jhipster.sample.domain.User_
import io.github.jhipster.sample.repository.BankAccountRepository
import io.github.jhipster.sample.service.dto.BankAccountCriteria
import io.github.jhipster.sample.service.dto.BankAccountDTO
import io.github.jhipster.sample.service.mapper.BankAccountMapper
import io.github.jhipster.service.QueryService
import javax.persistence.criteria.JoinType
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for executing complex queries for [BankAccount] entities in the database.
 * The main input is a [BankAccountCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [BankAccountDTO] or a [Page] of [BankAccountDTO] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class BankAccountQueryService(
    private val bankAccountRepository: BankAccountRepository,
    private val bankAccountMapper: BankAccountMapper
) : QueryService<BankAccount>() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Return a [MutableList] of [BankAccountDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: BankAccountCriteria?): MutableList<BankAccountDTO> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return bankAccountMapper.toDto(bankAccountRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [BankAccountDTO] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: BankAccountCriteria?, page: Pageable): Page<BankAccountDTO> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return bankAccountRepository.findAll(specification, page)
            .map(bankAccountMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: BankAccountCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return bankAccountRepository.count(specification)
    }

    /**
     * Function to convert [BankAccountCriteria] to a [Specification].
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching [Specification] of the entity.
     */
    protected fun createSpecification(criteria: BankAccountCriteria?): Specification<BankAccount?> {
        var specification: Specification<BankAccount?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildRangeSpecification(criteria.id, BankAccount_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, BankAccount_.name))
            }
            if (criteria.bankNumber != null) {
                specification = specification.and(buildRangeSpecification(criteria.bankNumber, BankAccount_.bankNumber))
            }
            if (criteria.agencyNumber != null) {
                specification = specification.and(buildRangeSpecification(criteria.agencyNumber, BankAccount_.agencyNumber))
            }
            if (criteria.lastOperationDuration != null) {
                specification = specification.and(buildRangeSpecification(criteria.lastOperationDuration, BankAccount_.lastOperationDuration))
            }
            if (criteria.meanOperationDuration != null) {
                specification = specification.and(buildRangeSpecification(criteria.meanOperationDuration, BankAccount_.meanOperationDuration))
            }
            if (criteria.balance != null) {
                specification = specification.and(buildRangeSpecification(criteria.balance, BankAccount_.balance))
            }
            if (criteria.openingDay != null) {
                specification = specification.and(buildRangeSpecification(criteria.openingDay, BankAccount_.openingDay))
            }
            if (criteria.lastOperationDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.lastOperationDate, BankAccount_.lastOperationDate))
            }
            if (criteria.active != null) {
                specification = specification.and(buildSpecification(criteria.active, BankAccount_.active))
            }
            if (criteria.accountType != null) {
                specification = specification.and(buildSpecification(criteria.accountType, BankAccount_.accountType))
            }
            if (criteria.userId != null) {
                specification = specification.and(buildSpecification(criteria.userId) {
                    it.join(BankAccount_.user, JoinType.LEFT).get(User_.id)
                })
            }
            if (criteria.operationId != null) {
                specification = specification.and(buildSpecification(criteria.operationId) {
                    it.join(BankAccount_.operations, JoinType.LEFT).get(Operation_.id)
                })
            }
        }
        return specification
    }
}
