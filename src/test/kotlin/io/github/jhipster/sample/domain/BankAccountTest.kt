package io.github.jhipster.sample.domain

import io.github.jhipster.sample.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BankAccountTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(BankAccount::class)
        val bankAccount1 = BankAccount()
        bankAccount1.id = 1L
        val bankAccount2 = BankAccount()
        bankAccount2.id = bankAccount1.id
        assertThat(bankAccount1).isEqualTo(bankAccount2)
        bankAccount2.id = 2L
        assertThat(bankAccount1).isNotEqualTo(bankAccount2)
        bankAccount1.id = null
        assertThat(bankAccount1).isNotEqualTo(bankAccount2)
    }
}
