package io.github.jhipster.sample.web.rest.errors

import java.io.Serializable

class FieldErrorVM(val objectName: String, val field: String, val message: String?) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
