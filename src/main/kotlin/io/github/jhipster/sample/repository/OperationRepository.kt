package io.github.jhipster.sample.repository

import io.github.jhipster.sample.domain.Operation
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Operation] entity.
 */
@Repository
interface OperationRepository : JpaRepository<Operation, Long> {

    @Query(value = "select distinct operation from Operation operation left join fetch operation.labels",
        countQuery = "select count(distinct operation) from Operation operation")
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Operation>

    @Query("select distinct operation from Operation operation left join fetch operation.labels")
    fun findAllWithEagerRelationships(): MutableList<Operation>

    @Query("select operation from Operation operation left join fetch operation.labels where operation.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Operation>
}
