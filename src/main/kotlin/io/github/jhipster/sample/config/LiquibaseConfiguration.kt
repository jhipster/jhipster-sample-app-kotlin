package io.github.jhipster.sample.config

import io.github.jhipster.config.JHipsterConstants
import io.github.jhipster.config.liquibase.SpringLiquibaseUtil
import java.util.concurrent.Executor
import javax.sql.DataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
class LiquibaseConfiguration(private val env: Environment) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun liquibase(
        @Qualifier("taskExecutor") executor: Executor,
        @LiquibaseDataSource liquibaseDataSource: ObjectProvider<DataSource>,
        liquibaseProperties: LiquibaseProperties,
        dataSource: ObjectProvider<DataSource>,
        dataSourceProperties: DataSourceProperties
    ) =
            // If you don't want Liquibase to start asynchronously, substitute by this:
            // SpringLiquibase liquibase = SpringLiquibaseUtil.createSpringLiquibase(liquibaseDataSource.getIfAvailable(), liquibaseProperties, dataSource.getIfUnique(), dataSourceProperties);
            SpringLiquibaseUtil.createAsyncSpringLiquibase(this.env, executor, liquibaseDataSource.getIfAvailable(), liquibaseProperties, dataSource.getIfUnique(), dataSourceProperties)
            .apply {
            changeLog = "classpath:config/liquibase/master.xml"
            contexts = liquibaseProperties.contexts
            defaultSchema = liquibaseProperties.defaultSchema
            liquibaseSchema = liquibaseProperties.liquibaseSchema
            liquibaseTablespace = liquibaseProperties.liquibaseTablespace
            databaseChangeLogLockTable = liquibaseProperties.databaseChangeLogLockTable
            databaseChangeLogTable = liquibaseProperties.databaseChangeLogTable
            isDropFirst = liquibaseProperties.isDropFirst
            labels = liquibaseProperties.labels
            setChangeLogParameters(liquibaseProperties.parameters)
            setRollbackFile(liquibaseProperties.rollbackFile)
            isTestRollbackOnUpdate = liquibaseProperties.isTestRollbackOnUpdate

            if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE))) {
                setShouldRun(false)
            } else {
                setShouldRun(liquibaseProperties.isEnabled)
                log.debug("Configuring Liquibase")
            }
        }
}
