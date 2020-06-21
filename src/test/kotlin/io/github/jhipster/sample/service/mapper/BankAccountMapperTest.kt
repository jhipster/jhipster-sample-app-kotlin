package io.github.jhipster.sample.service.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BankAccountMapperTest {

    private lateinit var bankAccountMapper: BankAccountMapper

    @BeforeEach
    fun setUp() {
        bankAccountMapper = BankAccountMapperImpl()
    }

    @Test
    fun testEntityFromId() {
        val id = 1L
        assertThat(bankAccountMapper.fromId(id)?.id).isEqualTo(id)
        assertThat(bankAccountMapper.fromId(null)).isNull()
    }
}
