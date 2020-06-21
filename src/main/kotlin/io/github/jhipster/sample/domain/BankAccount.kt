package io.github.jhipster.sample.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.github.jhipster.sample.domain.enumeration.BankAccountType
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A BankAccount.
 */
@Entity
@Table(name = "bank_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class BankAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "bank_number")
    var bankNumber: Int? = null,

    @Column(name = "agency_number")
    var agencyNumber: Long? = null,

    @Column(name = "last_operation_duration")
    var lastOperationDuration: Float? = null,

    @Column(name = "mean_operation_duration")
    var meanOperationDuration: Double? = null,

    @get: NotNull
    @Column(name = "balance", precision = 21, scale = 2, nullable = false)
    var balance: BigDecimal? = null,

    @Column(name = "opening_day")
    var openingDay: LocalDate? = null,

    @Column(name = "last_operation_date")
    var lastOperationDate: Instant? = null,

    @Column(name = "active")
    var active: Boolean? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    var accountType: BankAccountType? = null,

    @Lob
    @Column(name = "attachment")
    var attachment: ByteArray? = null,

    @Column(name = "attachment_content_type")
    var attachmentContentType: String? = null,

    @Lob
    @Column(name = "description")
    var description: String? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["bankAccounts"], allowSetters = true)
    var user: User? = null,

    @OneToMany(mappedBy = "bankAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var operations: MutableSet<Operation> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addOperation(operation: Operation): BankAccount {
        this.operations.add(operation)
        operation.bankAccount = this
        return this
    }

    fun removeOperation(operation: Operation): BankAccount {
        this.operations.remove(operation)
        operation.bankAccount = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BankAccount) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "BankAccount{" +
        "id=$id" +
        ", name='$name'" +
        ", bankNumber=$bankNumber" +
        ", agencyNumber=$agencyNumber" +
        ", lastOperationDuration=$lastOperationDuration" +
        ", meanOperationDuration=$meanOperationDuration" +
        ", balance=$balance" +
        ", openingDay='$openingDay'" +
        ", lastOperationDate='$lastOperationDate'" +
        ", active='$active'" +
        ", accountType='$accountType'" +
        ", attachment='$attachment'" +
        ", attachmentContentType='$attachmentContentType'" +
        ", description='$description'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
