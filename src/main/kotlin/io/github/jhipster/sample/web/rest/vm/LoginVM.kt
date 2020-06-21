package io.github.jhipster.sample.web.rest.vm

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * View Model object for storing a user's credentials.
 */
class LoginVM(
    @field:NotNull
    @field:Size(min = 1, max = 50)
    var username: String? = null,

    @field:NotNull
    @field:Size(min = 4, max = 100)
    var password: String? = null,

    var isRememberMe: Boolean? = null
) {
    override fun toString() = "LoginVM{" +
        "username='" + username + '\''.toString() +
        ", rememberMe=" + isRememberMe +
        '}'.toString()
}
