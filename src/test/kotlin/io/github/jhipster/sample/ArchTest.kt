package io.github.jhipster.sample

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("io.github.jhipster.sample")

        noClasses()
            .that()
                .resideInAnyPackage("io.github.jhipster.sample.service..")
            .or()
                .resideInAnyPackage("io.github.jhipster.sample.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..io.github.jhipster.sample.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
