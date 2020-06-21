package io.github.jhipster.sample.web.rest.errors

import java.net.URI
import org.zalando.problem.AbstractThrowableProblem
import org.zalando.problem.Exceptional
import org.zalando.problem.Status

open class BadRequestAlertException(type: URI, defaultMessage: String, val entityName: String, val errorKey: String) :
    AbstractThrowableProblem(
        type, defaultMessage, Status.BAD_REQUEST, null, null, null,
        getAlertParameters(entityName, errorKey)
    ) {

    constructor(defaultMessage: String, entityName: String, errorKey: String) :
        this(DEFAULT_TYPE, defaultMessage, entityName, errorKey)

    override fun getCause(): Exceptional? = super.cause

    companion object {

        private const val serialVersionUID = 1L

        private fun getAlertParameters(entityName: String, errorKey: String) =
            mutableMapOf<String, Any>(
                "message" to "error.$errorKey",
                "params" to entityName
            )
    }
}
