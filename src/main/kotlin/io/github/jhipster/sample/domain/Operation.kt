package io.github.jhipster.sample.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Operation.
 */
@Entity
@Table(name = "operation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Operation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "date", nullable = false)
    var date: Instant? = null,

    @Column(name = "description")
    var description: String? = null,

    @get: NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    var amount: BigDecimal? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["operations"], allowSetters = true)
    var bankAccount: BankAccount? = null,

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "operation_label",
        joinColumns = [JoinColumn(name = "operation_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "label_id", referencedColumnName = "id")])
    var labels: MutableSet<Label> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addLabel(label: Label): Operation {
        this.labels.add(label)
        return this
    }

    fun removeLabel(label: Label): Operation {
        this.labels.remove(label)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Operation) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Operation{" +
        "id=$id" +
        ", date='$date'" +
        ", description='$description'" +
        ", amount=$amount" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
