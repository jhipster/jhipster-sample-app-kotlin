package io.github.jhipster.sample.service.mapper

/**
 * Contract for a generic dto to entity mapper.
 *
 * @param D DTO type parameter.
 * @param E Entity type parameter.
 */

interface EntityMapper<D, E> {

    fun toEntity(dto: D): E

    fun toDto(entity: E): D

    fun toEntity(dtoList: MutableList<D>): MutableList<E>

    fun toDto(entityList: MutableList<E>): MutableList<D>
}
