package io.github.jhipster.sample.config.timezone

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.repository.timezone.DateTimeWrapper
import io.github.jhipster.sample.repository.timezone.DateTimeWrapperRepository
import java.lang.String.format
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.rowset.SqlRowSet
import org.springframework.transaction.annotation.Transactional

/**
 * Integration tests for the UTC Hibernate configuration.
 */
@SpringBootTest(classes = [JhipsterApp::class])
class HibernateTimeZoneIT {

    @Autowired
    private lateinit var dateTimeWrapperRepository: DateTimeWrapperRepository
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var dateTimeWrapper: DateTimeWrapper
    private lateinit var dateTimeFormatter: DateTimeFormatter
    private lateinit var timeFormatter: DateTimeFormatter
    private lateinit var dateFormatter: DateTimeFormatter

    @BeforeEach
    fun setup() {
        dateTimeWrapper = DateTimeWrapper(
            instant = Instant.parse("2014-11-12T05:50:00.0Z"),
            localDateTime = LocalDateTime.parse("2014-11-12T07:50:00.0"),
            offsetDateTime = OffsetDateTime.parse("2011-12-14T08:30:00.0Z"),
            zonedDateTime = ZonedDateTime.parse("2011-12-14T08:30:00.0Z"),
            localTime = LocalTime.parse("14:30:00"),
            offsetTime = OffsetTime.parse("14:30:00+02:00"),
            localDate = LocalDate.parse("2016-09-10")
        )

        dateTimeFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.S")
            .withZone(ZoneId.of("UTC"))

        timeFormatter = DateTimeFormatter
            .ofPattern("HH:mm:ss")
            .withZone(ZoneId.of("UTC"))

        dateFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd")
    }

    @Test
    @Transactional
    fun storeInstantWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        dateTimeWrapperRepository.saveAndFlush(dateTimeWrapper)

        val request = generateSqlRequest("instant", dateTimeWrapper.id!!)
        val resultSet = jdbcTemplate.queryForRowSet(request)
        val expectedValue = dateTimeFormatter.format(dateTimeWrapper.instant)

        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue)
    }

    @Test
    @Transactional
    fun storeLocalDateTimeWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        dateTimeWrapperRepository.saveAndFlush(dateTimeWrapper)

        val request = generateSqlRequest("local_date_time", dateTimeWrapper.id!!)
        val resultSet = jdbcTemplate.queryForRowSet(request)
        val expectedValue = dateTimeWrapper
            .localDateTime
            ?.atZone(ZoneId.systemDefault())
            ?.format(dateTimeFormatter)

        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue)
    }

    @Test
    @Transactional
    fun storeOffsetDateTimeWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        dateTimeWrapperRepository.saveAndFlush(dateTimeWrapper)

        val request = generateSqlRequest("offset_date_time", dateTimeWrapper.id!!)
        val resultSet = jdbcTemplate.queryForRowSet(request)
        val expectedValue = dateTimeWrapper
            .offsetDateTime
            ?.format(dateTimeFormatter)

        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue)
    }

    @Test
    @Transactional
    fun storeZoneDateTimeWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        dateTimeWrapperRepository.saveAndFlush(dateTimeWrapper)

        val request = generateSqlRequest("zoned_date_time", dateTimeWrapper.id!!)
        val resultSet = jdbcTemplate.queryForRowSet(request)
        val expectedValue = dateTimeWrapper
            .zonedDateTime
            ?.format(dateTimeFormatter)

        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue)
    }

    @Test
    @Transactional
    fun storeLocalTimeWithUtcConfigShouldBeStoredOnGMTTimeZoneAccordingToHis1stJan1970Value() {
        dateTimeWrapperRepository.saveAndFlush(dateTimeWrapper)

        val request = generateSqlRequest("local_time", dateTimeWrapper.id!!)
        val resultSet = jdbcTemplate.queryForRowSet(request)
        val expectedValue = dateTimeWrapper
            .localTime
            ?.atDate(LocalDate.of(1970, Month.JANUARY, 1))
            ?.atZone(ZoneId.systemDefault())
            ?.format(timeFormatter)

        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue)
    }

    @Test
    @Transactional
    fun storeOffsetTimeWithUtcConfigShouldBeStoredOnGMTTimeZoneAccordingToHis1stJan1970Value() {
        dateTimeWrapperRepository.saveAndFlush(dateTimeWrapper)

        val request = generateSqlRequest("offset_time", dateTimeWrapper.id!!)
        val resultSet = jdbcTemplate.queryForRowSet(request)
        val expectedValue = dateTimeWrapper
            .offsetTime
            ?.toLocalTime()
            ?.atDate(LocalDate.of(1970, Month.JANUARY, 1))
            ?.atZone(ZoneId.systemDefault())
            ?.format(timeFormatter)

        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue)
    }

    @Test
    @Transactional
    fun storeLocalDateWithUtcConfigShouldBeStoredWithoutTransformation() {
        dateTimeWrapperRepository.saveAndFlush(dateTimeWrapper)

        val request = generateSqlRequest("local_date", dateTimeWrapper.id!!)
        val resultSet = jdbcTemplate.queryForRowSet(request)
        val expectedValue = dateTimeWrapper
            .localDate
            ?.format(dateFormatter)

        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue)
    }

    private fun generateSqlRequest(fieldName: String, id: Long): String =
        format("SELECT %s FROM jhi_date_time_wrapper where id=%d", fieldName, id)

    private fun assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(
        sqlRowSet: SqlRowSet,
        expectedValue: String?
    ) {
        while (sqlRowSet.next()) {
            val dbValue = sqlRowSet.getString(1)

            assertThat(dbValue).isNotNull()
            assertThat(dbValue).isEqualTo(expectedValue)
        }
    }
}
