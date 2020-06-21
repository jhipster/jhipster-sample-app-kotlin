package io.github.jhipster.sample.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Label.
 */
@Entity
@Table(name = "label")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Label(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @get: Size(min = 3)
    @Column(name = "label_name", nullable = false)
    var labelName: String? = null,

    @ManyToMany(mappedBy = "labels")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    var operations: MutableSet<Operation> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addOperation(operation: Operation): Label {
        this.operations.add(operation)
        operation.labels.add(this)
        return this
    }

    fun removeOperation(operation: Operation): Label {
        this.operations.remove(operation)
        operation.labels.remove(this)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Label) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Label{" +
        "id=$id" +
        ", labelName='$labelName'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
