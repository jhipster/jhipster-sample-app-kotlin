package io.github.jhipster.sample.web.rest.errors

import org.zalando.problem.AbstractThrowableProblem
import org.zalando.problem.Exceptional
import org.zalando.problem.Status

class InvalidPasswordException :
    AbstractThrowableProblem(INVALID_PASSWORD_TYPE, "Incorrect password", Status.BAD_REQUEST) {

    override fun getCause(): Exceptional? = super.cause

    companion object {
        private const val serialVersionUID = 1L
    }
}
