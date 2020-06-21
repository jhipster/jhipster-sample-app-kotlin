package io.github.jhipster.sample.service

class InvalidPasswordException : RuntimeException("Incorrect password") {
    companion object {
        private const val serialVersionUID = 1L
    }
}
