package io.github.jhipster.sample.service.dto

import io.github.jhipster.sample.domain.enumeration.BankAccountType
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.BigDecimalFilter
import io.github.jhipster.service.filter.BooleanFilter
import io.github.jhipster.service.filter.DoubleFilter
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.FloatFilter
import io.github.jhipster.service.filter.InstantFilter
import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LocalDateFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [io.github.jhipster.sample.domain.BankAccount] entity. This class is used in
 * [io.github.jhipster.sample.web.rest.BankAccountResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/bank-accounts?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class BankAccountCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var bankNumber: IntegerFilter? = null,

    var agencyNumber: LongFilter? = null,

    var lastOperationDuration: FloatFilter? = null,

    var meanOperationDuration: DoubleFilter? = null,

    var balance: BigDecimalFilter? = null,

    var openingDay: LocalDateFilter? = null,

    var lastOperationDate: InstantFilter? = null,

    var active: BooleanFilter? = null,

    var accountType: BankAccountTypeFilter? = null,

    var userId: LongFilter? = null,

    var operationId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: BankAccountCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.bankNumber?.copy(),
            other.agencyNumber?.copy(),
            other.lastOperationDuration?.copy(),
            other.meanOperationDuration?.copy(),
            other.balance?.copy(),
            other.openingDay?.copy(),
            other.lastOperationDate?.copy(),
            other.active?.copy(),
            other.accountType?.copy(),
            other.userId?.copy(),
            other.operationId?.copy()
        )

    /**
     * Class for filtering BankAccountType
     */
    class BankAccountTypeFilter : Filter<BankAccountType> {
        constructor()

        constructor(filter: BankAccountTypeFilter) : super(filter)

        override fun copy() = BankAccountTypeFilter(this)
    }

    override fun copy() = BankAccountCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
