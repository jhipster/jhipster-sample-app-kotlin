package io.github.jhipster.sample.service.mapper

import io.github.jhipster.sample.domain.BankAccount
import io.github.jhipster.sample.service.dto.BankAccountDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [BankAccount] and its DTO [BankAccountDTO].
 */
@Mapper(componentModel = "spring", uses = [UserMapper::class])
interface BankAccountMapper :
    EntityMapper<BankAccountDTO, BankAccount> {

    @Mappings(
        Mapping(source = "user.id", target = "userId"),
        Mapping(source = "user.login", target = "userLogin")
    )
    override fun toDto(bankAccount: BankAccount): BankAccountDTO

    @Mappings(
        Mapping(source = "userId", target = "user"),
        Mapping(target = "operations", ignore = true),
        Mapping(target = "removeOperation", ignore = true)
    )
    override fun toEntity(bankAccountDTO: BankAccountDTO): BankAccount

    @JvmDefault
    fun fromId(id: Long?) = id?.let {
        val bankAccount = BankAccount()
        bankAccount.id = id
        bankAccount
    }
}
