package io.github.jhipster.sample.domain

import io.github.jhipster.sample.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LabelTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Label::class)
        val label1 = Label()
        label1.id = 1L
        val label2 = Label()
        label2.id = label1.id
        assertThat(label1).isEqualTo(label2)
        label2.id = 2L
        assertThat(label1).isNotEqualTo(label2)
        label1.id = null
        assertThat(label1).isNotEqualTo(label2)
    }
}
