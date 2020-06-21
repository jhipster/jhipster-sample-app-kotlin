package io.github.jhipster.sample.service.dto

import io.github.jhipster.sample.domain.enumeration.BankAccountType
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import javax.persistence.Lob
import javax.validation.constraints.NotNull

/**
 * A DTO for the [io.github.jhipster.sample.domain.BankAccount] entity.
 */
data class BankAccountDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    var bankNumber: Int? = null,

    var agencyNumber: Long? = null,

    var lastOperationDuration: Float? = null,

    var meanOperationDuration: Double? = null,

    @get: NotNull
    var balance: BigDecimal? = null,

    var openingDay: LocalDate? = null,

    var lastOperationDate: Instant? = null,

    var active: Boolean? = null,

    var accountType: BankAccountType? = null,

    @Lob
    var attachment: ByteArray? = null,
    var attachmentContentType: String? = null,

    @Lob
    var description: String? = null,

    var userId: Long? = null,

    var userLogin: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BankAccountDTO) return false
        return id != null && id == other.id
    }

    override fun hashCode() = 31
}
