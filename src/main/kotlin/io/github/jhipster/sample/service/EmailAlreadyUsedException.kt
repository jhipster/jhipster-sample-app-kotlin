package io.github.jhipster.sample.service

class EmailAlreadyUsedException : RuntimeException("Email is already in use!") {
    companion object {
        private const val serialVersionUID = 1L
    }
}
