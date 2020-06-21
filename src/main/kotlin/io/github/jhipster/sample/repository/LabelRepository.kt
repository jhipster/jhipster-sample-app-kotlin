package io.github.jhipster.sample.repository

import io.github.jhipster.sample.domain.Label
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Label] entity.
 */
@Suppress("unused")
@Repository
interface LabelRepository : JpaRepository<Label, Long>
