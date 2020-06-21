package io.github.jhipster.sample.domain

import io.github.jhipster.sample.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OperationTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Operation::class)
        val operation1 = Operation()
        operation1.id = 1L
        val operation2 = Operation()
        operation2.id = operation1.id
        assertThat(operation1).isEqualTo(operation2)
        operation2.id = 2L
        assertThat(operation1).isNotEqualTo(operation2)
        operation1.id = null
        assertThat(operation1).isNotEqualTo(operation2)
    }
}
