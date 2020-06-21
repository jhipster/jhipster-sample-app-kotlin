package io.github.jhipster.sample.web.rest.errors

import org.zalando.problem.Exceptional

class LoginAlreadyUsedException :
    BadRequestAlertException(LOGIN_ALREADY_USED_TYPE, "Login name already used!", "userManagement", "userexists") {

    override fun getCause(): Exceptional? = super.cause

    companion object {
        private const val serialVersionUID = 1L
    }
}
