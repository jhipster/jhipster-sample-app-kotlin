package io.github.jhipster.sample.web.rest.errors

import java.net.URI

const val ERR_CONCURRENCY_FAILURE: String = "error.concurrencyFailure"
const val ERR_VALIDATION: String = "error.validation"
const val PROBLEM_BASE_URL: String = "https://www.jhipster.tech/problem"
@JvmField
val DEFAULT_TYPE: URI = URI.create("$PROBLEM_BASE_URL/problem-with-message")
@JvmField
val CONSTRAINT_VIOLATION_TYPE: URI = URI.create("$PROBLEM_BASE_URL/constraint-violation")
@JvmField
val INVALID_PASSWORD_TYPE: URI = URI.create("$PROBLEM_BASE_URL/invalid-password")
@JvmField
val EMAIL_ALREADY_USED_TYPE: URI = URI.create("$PROBLEM_BASE_URL/email-already-used")
@JvmField
val LOGIN_ALREADY_USED_TYPE: URI = URI.create("$PROBLEM_BASE_URL/login-already-used")
