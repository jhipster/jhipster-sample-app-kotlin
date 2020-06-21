package io.github.jhipster.sample.service.dto

import io.github.jhipster.sample.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BankAccountDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(BankAccountDTO::class)
        val bankAccountDTO1 = BankAccountDTO()
        bankAccountDTO1.id = 1L
        val bankAccountDTO2 = BankAccountDTO()
        assertThat(bankAccountDTO1).isNotEqualTo(bankAccountDTO2)
        bankAccountDTO2.id = bankAccountDTO1.id
        assertThat(bankAccountDTO1).isEqualTo(bankAccountDTO2)
        bankAccountDTO2.id = 2L
        assertThat(bankAccountDTO1).isNotEqualTo(bankAccountDTO2)
        bankAccountDTO1.id = null
        assertThat(bankAccountDTO1).isNotEqualTo(bankAccountDTO2)
    }
}
