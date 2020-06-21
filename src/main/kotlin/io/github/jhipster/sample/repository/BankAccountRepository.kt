package io.github.jhipster.sample.repository

import io.github.jhipster.sample.domain.BankAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [BankAccount] entity.
 */
@Suppress("unused")
@Repository
interface BankAccountRepository : JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {

    @Query("select bankAccount from BankAccount bankAccount where bankAccount.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<BankAccount>
}
