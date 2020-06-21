package io.github.jhipster.sample.security

import org.springframework.security.core.AuthenticationException

/**
 * This exception is thrown in case of a not activated user trying to authenticate.
 */
class UserNotActivatedException(message: String, t: Throwable? = null) : AuthenticationException(message, t) {

    companion object {
        private const val serialVersionUID = 1L
    }
}
