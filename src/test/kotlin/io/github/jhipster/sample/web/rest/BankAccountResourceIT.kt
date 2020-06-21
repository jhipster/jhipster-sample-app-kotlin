package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.domain.BankAccount
import io.github.jhipster.sample.domain.enumeration.BankAccountType
import io.github.jhipster.sample.repository.BankAccountRepository
import io.github.jhipster.sample.service.BankAccountQueryService
import io.github.jhipster.sample.service.BankAccountService
import io.github.jhipster.sample.service.mapper.BankAccountMapper
import io.github.jhipster.sample.web.rest.errors.ExceptionTranslator
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Base64Utils
import org.springframework.validation.Validator

/**
 * Integration tests for the [BankAccountResource] REST controller.
 *
 * @see BankAccountResource
 */
@SpringBootTest(classes = [JhipsterApp::class])
@AutoConfigureMockMvc
@WithMockUser
class BankAccountResourceIT {

    @Autowired
    private lateinit var bankAccountRepository: BankAccountRepository

    @Autowired
    private lateinit var bankAccountMapper: BankAccountMapper

    @Autowired
    private lateinit var bankAccountService: BankAccountService

    @Autowired
    private lateinit var bankAccountQueryService: BankAccountQueryService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restBankAccountMockMvc: MockMvc

    private lateinit var bankAccount: BankAccount

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val bankAccountResource = BankAccountResource(bankAccountService, bankAccountQueryService)
         this.restBankAccountMockMvc = MockMvcBuilders.standaloneSetup(bankAccountResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        bankAccount = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createBankAccount() {
        val databaseSizeBeforeCreate = bankAccountRepository.findAll().size

        // Create the BankAccount
        val bankAccountDTO = bankAccountMapper.toDto(bankAccount)
        restBankAccountMockMvc.perform(
            post("/api/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bankAccountDTO))
        ).andExpect(status().isCreated)

        // Validate the BankAccount in the database
        val bankAccountList = bankAccountRepository.findAll()
        assertThat(bankAccountList).hasSize(databaseSizeBeforeCreate + 1)
        val testBankAccount = bankAccountList[bankAccountList.size - 1]
        assertThat(testBankAccount.name).isEqualTo(DEFAULT_NAME)
        assertThat(testBankAccount.bankNumber).isEqualTo(DEFAULT_BANK_NUMBER)
        assertThat(testBankAccount.agencyNumber).isEqualTo(DEFAULT_AGENCY_NUMBER)
        assertThat(testBankAccount.lastOperationDuration).isEqualTo(DEFAULT_LAST_OPERATION_DURATION)
        assertThat(testBankAccount.meanOperationDuration).isEqualTo(DEFAULT_MEAN_OPERATION_DURATION)
        assertThat(testBankAccount.balance).isEqualTo(DEFAULT_BALANCE)
        assertThat(testBankAccount.openingDay).isEqualTo(DEFAULT_OPENING_DAY)
        assertThat(testBankAccount.lastOperationDate).isEqualTo(DEFAULT_LAST_OPERATION_DATE)
        assertThat(testBankAccount.active).isEqualTo(DEFAULT_ACTIVE)
        assertThat(testBankAccount.accountType).isEqualTo(DEFAULT_ACCOUNT_TYPE)
        assertThat(testBankAccount.attachment).isEqualTo(DEFAULT_ATTACHMENT)
        assertThat(testBankAccount.attachmentContentType).isEqualTo(DEFAULT_ATTACHMENT_CONTENT_TYPE)
        assertThat(testBankAccount.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    fun createBankAccountWithExistingId() {
        val databaseSizeBeforeCreate = bankAccountRepository.findAll().size

        // Create the BankAccount with an existing ID
        bankAccount.id = 1L
        val bankAccountDTO = bankAccountMapper.toDto(bankAccount)

        // An entity with an existing ID cannot be created, so this API call must fail
        restBankAccountMockMvc.perform(
            post("/api/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bankAccountDTO))
        ).andExpect(status().isBadRequest)

        // Validate the BankAccount in the database
        val bankAccountList = bankAccountRepository.findAll()
        assertThat(bankAccountList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = bankAccountRepository.findAll().size
        // set the field null
        bankAccount.name = null

        // Create the BankAccount, which fails.
        val bankAccountDTO = bankAccountMapper.toDto(bankAccount)

        restBankAccountMockMvc.perform(
            post("/api/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bankAccountDTO))
        ).andExpect(status().isBadRequest)

        val bankAccountList = bankAccountRepository.findAll()
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkBalanceIsRequired() {
        val databaseSizeBeforeTest = bankAccountRepository.findAll().size
        // set the field null
        bankAccount.balance = null

        // Create the BankAccount, which fails.
        val bankAccountDTO = bankAccountMapper.toDto(bankAccount)

        restBankAccountMockMvc.perform(
            post("/api/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bankAccountDTO))
        ).andExpect(status().isBadRequest)

        val bankAccountList = bankAccountRepository.findAll()
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccounts() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList
        restBankAccountMockMvc.perform(get("/api/bank-accounts?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].bankNumber").value(hasItem(DEFAULT_BANK_NUMBER)))
            .andExpect(jsonPath("$.[*].agencyNumber").value(hasItem(DEFAULT_AGENCY_NUMBER?.toInt())))
            .andExpect(jsonPath("$.[*].lastOperationDuration").value(hasItem(DEFAULT_LAST_OPERATION_DURATION.toDouble())))
            .andExpect(jsonPath("$.[*].meanOperationDuration").value(hasItem(DEFAULT_MEAN_OPERATION_DURATION.toDouble())))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE?.toInt())))
            .andExpect(jsonPath("$.[*].openingDay").value(hasItem(DEFAULT_OPENING_DAY.toString())))
            .andExpect(jsonPath("$.[*].lastOperationDate").value(hasItem(DEFAULT_LAST_OPERATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].attachmentContentType").value(hasItem(DEFAULT_ATTACHMENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].attachment").value(hasItem(Base64Utils.encodeToString(DEFAULT_ATTACHMENT))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString()))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getBankAccount() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        val id = bankAccount.id
        assertNotNull(id)

        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bankAccount.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.bankNumber").value(DEFAULT_BANK_NUMBER))
            .andExpect(jsonPath("$.agencyNumber").value(DEFAULT_AGENCY_NUMBER?.toInt()))
            .andExpect(jsonPath("$.lastOperationDuration").value(DEFAULT_LAST_OPERATION_DURATION.toDouble()))
            .andExpect(jsonPath("$.meanOperationDuration").value(DEFAULT_MEAN_OPERATION_DURATION.toDouble()))
            .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE?.toInt()))
            .andExpect(jsonPath("$.openingDay").value(DEFAULT_OPENING_DAY.toString()))
            .andExpect(jsonPath("$.lastOperationDate").value(DEFAULT_LAST_OPERATION_DATE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.accountType").value(DEFAULT_ACCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.attachmentContentType").value(DEFAULT_ATTACHMENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.attachment").value(Base64Utils.encodeToString(DEFAULT_ATTACHMENT)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString())) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getBankAccountsByIdFiltering() {
      // Initialize the database
      bankAccountRepository.saveAndFlush(bankAccount)
      val id = bankAccount.id

      defaultBankAccountShouldBeFound("id.equals=" + id)
      defaultBankAccountShouldNotBeFound("id.notEquals=" + id)

      defaultBankAccountShouldBeFound("id.greaterThanOrEqual=" + id)
      defaultBankAccountShouldNotBeFound("id.greaterThan=" + id)

      defaultBankAccountShouldBeFound("id.lessThanOrEqual=" + id)
      defaultBankAccountShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByNameIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where name equals to DEFAULT_NAME
        defaultBankAccountShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the bankAccountList where name equals to UPDATED_NAME
        defaultBankAccountShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByNameIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where name not equals to DEFAULT_NAME
        defaultBankAccountShouldNotBeFound("name.notEquals=" + DEFAULT_NAME)

        // Get all the bankAccountList where name not equals to UPDATED_NAME
        defaultBankAccountShouldBeFound("name.notEquals=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByNameIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBankAccountShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the bankAccountList where name equals to UPDATED_NAME
        defaultBankAccountShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByNameIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where name is not null
        defaultBankAccountShouldBeFound("name.specified=true")

        // Get all the bankAccountList where name is null
        defaultBankAccountShouldNotBeFound("name.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByNameContainsSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where name contains DEFAULT_NAME
        defaultBankAccountShouldBeFound("name.contains=" + DEFAULT_NAME)

        // Get all the bankAccountList where name contains UPDATED_NAME
        defaultBankAccountShouldNotBeFound("name.contains=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByNameNotContainsSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where name does not contain DEFAULT_NAME
        defaultBankAccountShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME)

        // Get all the bankAccountList where name does not contain UPDATED_NAME
        defaultBankAccountShouldBeFound("name.doesNotContain=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber equals to DEFAULT_BANK_NUMBER
        defaultBankAccountShouldBeFound("bankNumber.equals=$DEFAULT_BANK_NUMBER")

        // Get all the bankAccountList where bankNumber equals to UPDATED_BANK_NUMBER
        defaultBankAccountShouldNotBeFound("bankNumber.equals=$UPDATED_BANK_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber not equals to DEFAULT_BANK_NUMBER
        defaultBankAccountShouldNotBeFound("bankNumber.notEquals=" + DEFAULT_BANK_NUMBER)

        // Get all the bankAccountList where bankNumber not equals to UPDATED_BANK_NUMBER
        defaultBankAccountShouldBeFound("bankNumber.notEquals=" + UPDATED_BANK_NUMBER)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber in DEFAULT_BANK_NUMBER or UPDATED_BANK_NUMBER
        defaultBankAccountShouldBeFound("bankNumber.in=$DEFAULT_BANK_NUMBER,$UPDATED_BANK_NUMBER")

        // Get all the bankAccountList where bankNumber equals to UPDATED_BANK_NUMBER
        defaultBankAccountShouldNotBeFound("bankNumber.in=$UPDATED_BANK_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber is not null
        defaultBankAccountShouldBeFound("bankNumber.specified=true")

        // Get all the bankAccountList where bankNumber is null
        defaultBankAccountShouldNotBeFound("bankNumber.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber is greater than or equal to DEFAULT_BANK_NUMBER
        defaultBankAccountShouldBeFound("bankNumber.greaterThanOrEqual=$DEFAULT_BANK_NUMBER")

        // Get all the bankAccountList where bankNumber is greater than or equal to UPDATED_BANK_NUMBER
        defaultBankAccountShouldNotBeFound("bankNumber.greaterThanOrEqual=$UPDATED_BANK_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsLessThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber is less than or equal to DEFAULT_BANK_NUMBER
        defaultBankAccountShouldBeFound("bankNumber.lessThanOrEqual=$DEFAULT_BANK_NUMBER")

        // Get all the bankAccountList where bankNumber is less than or equal to SMALLER_BANK_NUMBER
        defaultBankAccountShouldNotBeFound("bankNumber.lessThanOrEqual=$SMALLER_BANK_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsLessThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber is less than DEFAULT_BANK_NUMBER
        defaultBankAccountShouldNotBeFound("bankNumber.lessThan=$DEFAULT_BANK_NUMBER")

        // Get all the bankAccountList where bankNumber is less than UPDATED_BANK_NUMBER
        defaultBankAccountShouldBeFound("bankNumber.lessThan=$UPDATED_BANK_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBankNumberIsGreaterThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where bankNumber is greater than DEFAULT_BANK_NUMBER
        defaultBankAccountShouldNotBeFound("bankNumber.greaterThan=$DEFAULT_BANK_NUMBER")

        // Get all the bankAccountList where bankNumber is greater than SMALLER_BANK_NUMBER
        defaultBankAccountShouldBeFound("bankNumber.greaterThan=$SMALLER_BANK_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber equals to DEFAULT_AGENCY_NUMBER
        defaultBankAccountShouldBeFound("agencyNumber.equals=$DEFAULT_AGENCY_NUMBER")

        // Get all the bankAccountList where agencyNumber equals to UPDATED_AGENCY_NUMBER
        defaultBankAccountShouldNotBeFound("agencyNumber.equals=$UPDATED_AGENCY_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber not equals to DEFAULT_AGENCY_NUMBER
        defaultBankAccountShouldNotBeFound("agencyNumber.notEquals=" + DEFAULT_AGENCY_NUMBER)

        // Get all the bankAccountList where agencyNumber not equals to UPDATED_AGENCY_NUMBER
        defaultBankAccountShouldBeFound("agencyNumber.notEquals=" + UPDATED_AGENCY_NUMBER)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber in DEFAULT_AGENCY_NUMBER or UPDATED_AGENCY_NUMBER
        defaultBankAccountShouldBeFound("agencyNumber.in=$DEFAULT_AGENCY_NUMBER,$UPDATED_AGENCY_NUMBER")

        // Get all the bankAccountList where agencyNumber equals to UPDATED_AGENCY_NUMBER
        defaultBankAccountShouldNotBeFound("agencyNumber.in=$UPDATED_AGENCY_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber is not null
        defaultBankAccountShouldBeFound("agencyNumber.specified=true")

        // Get all the bankAccountList where agencyNumber is null
        defaultBankAccountShouldNotBeFound("agencyNumber.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber is greater than or equal to DEFAULT_AGENCY_NUMBER
        defaultBankAccountShouldBeFound("agencyNumber.greaterThanOrEqual=$DEFAULT_AGENCY_NUMBER")

        // Get all the bankAccountList where agencyNumber is greater than or equal to UPDATED_AGENCY_NUMBER
        defaultBankAccountShouldNotBeFound("agencyNumber.greaterThanOrEqual=$UPDATED_AGENCY_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsLessThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber is less than or equal to DEFAULT_AGENCY_NUMBER
        defaultBankAccountShouldBeFound("agencyNumber.lessThanOrEqual=$DEFAULT_AGENCY_NUMBER")

        // Get all the bankAccountList where agencyNumber is less than or equal to SMALLER_AGENCY_NUMBER
        defaultBankAccountShouldNotBeFound("agencyNumber.lessThanOrEqual=$SMALLER_AGENCY_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsLessThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber is less than DEFAULT_AGENCY_NUMBER
        defaultBankAccountShouldNotBeFound("agencyNumber.lessThan=$DEFAULT_AGENCY_NUMBER")

        // Get all the bankAccountList where agencyNumber is less than UPDATED_AGENCY_NUMBER
        defaultBankAccountShouldBeFound("agencyNumber.lessThan=$UPDATED_AGENCY_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAgencyNumberIsGreaterThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where agencyNumber is greater than DEFAULT_AGENCY_NUMBER
        defaultBankAccountShouldNotBeFound("agencyNumber.greaterThan=$DEFAULT_AGENCY_NUMBER")

        // Get all the bankAccountList where agencyNumber is greater than SMALLER_AGENCY_NUMBER
        defaultBankAccountShouldBeFound("agencyNumber.greaterThan=$SMALLER_AGENCY_NUMBER")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration equals to DEFAULT_LAST_OPERATION_DURATION
        defaultBankAccountShouldBeFound("lastOperationDuration.equals=$DEFAULT_LAST_OPERATION_DURATION")

        // Get all the bankAccountList where lastOperationDuration equals to UPDATED_LAST_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("lastOperationDuration.equals=$UPDATED_LAST_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration not equals to DEFAULT_LAST_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("lastOperationDuration.notEquals=" + DEFAULT_LAST_OPERATION_DURATION)

        // Get all the bankAccountList where lastOperationDuration not equals to UPDATED_LAST_OPERATION_DURATION
        defaultBankAccountShouldBeFound("lastOperationDuration.notEquals=" + UPDATED_LAST_OPERATION_DURATION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration in DEFAULT_LAST_OPERATION_DURATION or UPDATED_LAST_OPERATION_DURATION
        defaultBankAccountShouldBeFound("lastOperationDuration.in=$DEFAULT_LAST_OPERATION_DURATION,$UPDATED_LAST_OPERATION_DURATION")

        // Get all the bankAccountList where lastOperationDuration equals to UPDATED_LAST_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("lastOperationDuration.in=$UPDATED_LAST_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration is not null
        defaultBankAccountShouldBeFound("lastOperationDuration.specified=true")

        // Get all the bankAccountList where lastOperationDuration is null
        defaultBankAccountShouldNotBeFound("lastOperationDuration.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration is greater than or equal to DEFAULT_LAST_OPERATION_DURATION
        defaultBankAccountShouldBeFound("lastOperationDuration.greaterThanOrEqual=$DEFAULT_LAST_OPERATION_DURATION")

        // Get all the bankAccountList where lastOperationDuration is greater than or equal to UPDATED_LAST_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("lastOperationDuration.greaterThanOrEqual=$UPDATED_LAST_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsLessThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration is less than or equal to DEFAULT_LAST_OPERATION_DURATION
        defaultBankAccountShouldBeFound("lastOperationDuration.lessThanOrEqual=$DEFAULT_LAST_OPERATION_DURATION")

        // Get all the bankAccountList where lastOperationDuration is less than or equal to SMALLER_LAST_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("lastOperationDuration.lessThanOrEqual=$SMALLER_LAST_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsLessThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration is less than DEFAULT_LAST_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("lastOperationDuration.lessThan=$DEFAULT_LAST_OPERATION_DURATION")

        // Get all the bankAccountList where lastOperationDuration is less than UPDATED_LAST_OPERATION_DURATION
        defaultBankAccountShouldBeFound("lastOperationDuration.lessThan=$UPDATED_LAST_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDurationIsGreaterThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDuration is greater than DEFAULT_LAST_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("lastOperationDuration.greaterThan=$DEFAULT_LAST_OPERATION_DURATION")

        // Get all the bankAccountList where lastOperationDuration is greater than SMALLER_LAST_OPERATION_DURATION
        defaultBankAccountShouldBeFound("lastOperationDuration.greaterThan=$SMALLER_LAST_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration equals to DEFAULT_MEAN_OPERATION_DURATION
        defaultBankAccountShouldBeFound("meanOperationDuration.equals=$DEFAULT_MEAN_OPERATION_DURATION")

        // Get all the bankAccountList where meanOperationDuration equals to UPDATED_MEAN_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("meanOperationDuration.equals=$UPDATED_MEAN_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration not equals to DEFAULT_MEAN_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("meanOperationDuration.notEquals=" + DEFAULT_MEAN_OPERATION_DURATION)

        // Get all the bankAccountList where meanOperationDuration not equals to UPDATED_MEAN_OPERATION_DURATION
        defaultBankAccountShouldBeFound("meanOperationDuration.notEquals=" + UPDATED_MEAN_OPERATION_DURATION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration in DEFAULT_MEAN_OPERATION_DURATION or UPDATED_MEAN_OPERATION_DURATION
        defaultBankAccountShouldBeFound("meanOperationDuration.in=$DEFAULT_MEAN_OPERATION_DURATION,$UPDATED_MEAN_OPERATION_DURATION")

        // Get all the bankAccountList where meanOperationDuration equals to UPDATED_MEAN_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("meanOperationDuration.in=$UPDATED_MEAN_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration is not null
        defaultBankAccountShouldBeFound("meanOperationDuration.specified=true")

        // Get all the bankAccountList where meanOperationDuration is null
        defaultBankAccountShouldNotBeFound("meanOperationDuration.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration is greater than or equal to DEFAULT_MEAN_OPERATION_DURATION
        defaultBankAccountShouldBeFound("meanOperationDuration.greaterThanOrEqual=$DEFAULT_MEAN_OPERATION_DURATION")

        // Get all the bankAccountList where meanOperationDuration is greater than or equal to UPDATED_MEAN_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("meanOperationDuration.greaterThanOrEqual=$UPDATED_MEAN_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsLessThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration is less than or equal to DEFAULT_MEAN_OPERATION_DURATION
        defaultBankAccountShouldBeFound("meanOperationDuration.lessThanOrEqual=$DEFAULT_MEAN_OPERATION_DURATION")

        // Get all the bankAccountList where meanOperationDuration is less than or equal to SMALLER_MEAN_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("meanOperationDuration.lessThanOrEqual=$SMALLER_MEAN_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsLessThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration is less than DEFAULT_MEAN_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("meanOperationDuration.lessThan=$DEFAULT_MEAN_OPERATION_DURATION")

        // Get all the bankAccountList where meanOperationDuration is less than UPDATED_MEAN_OPERATION_DURATION
        defaultBankAccountShouldBeFound("meanOperationDuration.lessThan=$UPDATED_MEAN_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByMeanOperationDurationIsGreaterThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where meanOperationDuration is greater than DEFAULT_MEAN_OPERATION_DURATION
        defaultBankAccountShouldNotBeFound("meanOperationDuration.greaterThan=$DEFAULT_MEAN_OPERATION_DURATION")

        // Get all the bankAccountList where meanOperationDuration is greater than SMALLER_MEAN_OPERATION_DURATION
        defaultBankAccountShouldBeFound("meanOperationDuration.greaterThan=$SMALLER_MEAN_OPERATION_DURATION")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance equals to DEFAULT_BALANCE
        defaultBankAccountShouldBeFound("balance.equals=$DEFAULT_BALANCE")

        // Get all the bankAccountList where balance equals to UPDATED_BALANCE
        defaultBankAccountShouldNotBeFound("balance.equals=$UPDATED_BALANCE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance not equals to DEFAULT_BALANCE
        defaultBankAccountShouldNotBeFound("balance.notEquals=" + DEFAULT_BALANCE)

        // Get all the bankAccountList where balance not equals to UPDATED_BALANCE
        defaultBankAccountShouldBeFound("balance.notEquals=" + UPDATED_BALANCE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance in DEFAULT_BALANCE or UPDATED_BALANCE
        defaultBankAccountShouldBeFound("balance.in=$DEFAULT_BALANCE,$UPDATED_BALANCE")

        // Get all the bankAccountList where balance equals to UPDATED_BALANCE
        defaultBankAccountShouldNotBeFound("balance.in=$UPDATED_BALANCE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance is not null
        defaultBankAccountShouldBeFound("balance.specified=true")

        // Get all the bankAccountList where balance is null
        defaultBankAccountShouldNotBeFound("balance.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance is greater than or equal to DEFAULT_BALANCE
        defaultBankAccountShouldBeFound("balance.greaterThanOrEqual=$DEFAULT_BALANCE")

        // Get all the bankAccountList where balance is greater than or equal to UPDATED_BALANCE
        defaultBankAccountShouldNotBeFound("balance.greaterThanOrEqual=$UPDATED_BALANCE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsLessThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance is less than or equal to DEFAULT_BALANCE
        defaultBankAccountShouldBeFound("balance.lessThanOrEqual=$DEFAULT_BALANCE")

        // Get all the bankAccountList where balance is less than or equal to SMALLER_BALANCE
        defaultBankAccountShouldNotBeFound("balance.lessThanOrEqual=$SMALLER_BALANCE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsLessThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance is less than DEFAULT_BALANCE
        defaultBankAccountShouldNotBeFound("balance.lessThan=$DEFAULT_BALANCE")

        // Get all the bankAccountList where balance is less than UPDATED_BALANCE
        defaultBankAccountShouldBeFound("balance.lessThan=$UPDATED_BALANCE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByBalanceIsGreaterThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where balance is greater than DEFAULT_BALANCE
        defaultBankAccountShouldNotBeFound("balance.greaterThan=$DEFAULT_BALANCE")

        // Get all the bankAccountList where balance is greater than SMALLER_BALANCE
        defaultBankAccountShouldBeFound("balance.greaterThan=$SMALLER_BALANCE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay equals to DEFAULT_OPENING_DAY
        defaultBankAccountShouldBeFound("openingDay.equals=$DEFAULT_OPENING_DAY")

        // Get all the bankAccountList where openingDay equals to UPDATED_OPENING_DAY
        defaultBankAccountShouldNotBeFound("openingDay.equals=$UPDATED_OPENING_DAY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay not equals to DEFAULT_OPENING_DAY
        defaultBankAccountShouldNotBeFound("openingDay.notEquals=" + DEFAULT_OPENING_DAY)

        // Get all the bankAccountList where openingDay not equals to UPDATED_OPENING_DAY
        defaultBankAccountShouldBeFound("openingDay.notEquals=" + UPDATED_OPENING_DAY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay in DEFAULT_OPENING_DAY or UPDATED_OPENING_DAY
        defaultBankAccountShouldBeFound("openingDay.in=$DEFAULT_OPENING_DAY,$UPDATED_OPENING_DAY")

        // Get all the bankAccountList where openingDay equals to UPDATED_OPENING_DAY
        defaultBankAccountShouldNotBeFound("openingDay.in=$UPDATED_OPENING_DAY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay is not null
        defaultBankAccountShouldBeFound("openingDay.specified=true")

        // Get all the bankAccountList where openingDay is null
        defaultBankAccountShouldNotBeFound("openingDay.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay is greater than or equal to DEFAULT_OPENING_DAY
        defaultBankAccountShouldBeFound("openingDay.greaterThanOrEqual=$DEFAULT_OPENING_DAY")

        // Get all the bankAccountList where openingDay is greater than or equal to UPDATED_OPENING_DAY
        defaultBankAccountShouldNotBeFound("openingDay.greaterThanOrEqual=$UPDATED_OPENING_DAY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsLessThanOrEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay is less than or equal to DEFAULT_OPENING_DAY
        defaultBankAccountShouldBeFound("openingDay.lessThanOrEqual=$DEFAULT_OPENING_DAY")

        // Get all the bankAccountList where openingDay is less than or equal to SMALLER_OPENING_DAY
        defaultBankAccountShouldNotBeFound("openingDay.lessThanOrEqual=$SMALLER_OPENING_DAY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsLessThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay is less than DEFAULT_OPENING_DAY
        defaultBankAccountShouldNotBeFound("openingDay.lessThan=$DEFAULT_OPENING_DAY")

        // Get all the bankAccountList where openingDay is less than UPDATED_OPENING_DAY
        defaultBankAccountShouldBeFound("openingDay.lessThan=$UPDATED_OPENING_DAY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOpeningDayIsGreaterThanSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where openingDay is greater than DEFAULT_OPENING_DAY
        defaultBankAccountShouldNotBeFound("openingDay.greaterThan=$DEFAULT_OPENING_DAY")

        // Get all the bankAccountList where openingDay is greater than SMALLER_OPENING_DAY
        defaultBankAccountShouldBeFound("openingDay.greaterThan=$SMALLER_OPENING_DAY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDateIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDate equals to DEFAULT_LAST_OPERATION_DATE
        defaultBankAccountShouldBeFound("lastOperationDate.equals=$DEFAULT_LAST_OPERATION_DATE")

        // Get all the bankAccountList where lastOperationDate equals to UPDATED_LAST_OPERATION_DATE
        defaultBankAccountShouldNotBeFound("lastOperationDate.equals=$UPDATED_LAST_OPERATION_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDateIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDate not equals to DEFAULT_LAST_OPERATION_DATE
        defaultBankAccountShouldNotBeFound("lastOperationDate.notEquals=" + DEFAULT_LAST_OPERATION_DATE)

        // Get all the bankAccountList where lastOperationDate not equals to UPDATED_LAST_OPERATION_DATE
        defaultBankAccountShouldBeFound("lastOperationDate.notEquals=" + UPDATED_LAST_OPERATION_DATE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDateIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDate in DEFAULT_LAST_OPERATION_DATE or UPDATED_LAST_OPERATION_DATE
        defaultBankAccountShouldBeFound("lastOperationDate.in=$DEFAULT_LAST_OPERATION_DATE,$UPDATED_LAST_OPERATION_DATE")

        // Get all the bankAccountList where lastOperationDate equals to UPDATED_LAST_OPERATION_DATE
        defaultBankAccountShouldNotBeFound("lastOperationDate.in=$UPDATED_LAST_OPERATION_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByLastOperationDateIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where lastOperationDate is not null
        defaultBankAccountShouldBeFound("lastOperationDate.specified=true")

        // Get all the bankAccountList where lastOperationDate is null
        defaultBankAccountShouldNotBeFound("lastOperationDate.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByActiveIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where active equals to DEFAULT_ACTIVE
        defaultBankAccountShouldBeFound("active.equals=$DEFAULT_ACTIVE")

        // Get all the bankAccountList where active equals to UPDATED_ACTIVE
        defaultBankAccountShouldNotBeFound("active.equals=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByActiveIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where active not equals to DEFAULT_ACTIVE
        defaultBankAccountShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE)

        // Get all the bankAccountList where active not equals to UPDATED_ACTIVE
        defaultBankAccountShouldBeFound("active.notEquals=" + UPDATED_ACTIVE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByActiveIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultBankAccountShouldBeFound("active.in=$DEFAULT_ACTIVE,$UPDATED_ACTIVE")

        // Get all the bankAccountList where active equals to UPDATED_ACTIVE
        defaultBankAccountShouldNotBeFound("active.in=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByActiveIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where active is not null
        defaultBankAccountShouldBeFound("active.specified=true")

        // Get all the bankAccountList where active is null
        defaultBankAccountShouldNotBeFound("active.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAccountTypeIsEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where accountType equals to DEFAULT_ACCOUNT_TYPE
        defaultBankAccountShouldBeFound("accountType.equals=$DEFAULT_ACCOUNT_TYPE")

        // Get all the bankAccountList where accountType equals to UPDATED_ACCOUNT_TYPE
        defaultBankAccountShouldNotBeFound("accountType.equals=$UPDATED_ACCOUNT_TYPE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAccountTypeIsNotEqualToSomething() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where accountType not equals to DEFAULT_ACCOUNT_TYPE
        defaultBankAccountShouldNotBeFound("accountType.notEquals=" + DEFAULT_ACCOUNT_TYPE)

        // Get all the bankAccountList where accountType not equals to UPDATED_ACCOUNT_TYPE
        defaultBankAccountShouldBeFound("accountType.notEquals=" + UPDATED_ACCOUNT_TYPE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAccountTypeIsInShouldWork() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where accountType in DEFAULT_ACCOUNT_TYPE or UPDATED_ACCOUNT_TYPE
        defaultBankAccountShouldBeFound("accountType.in=$DEFAULT_ACCOUNT_TYPE,$UPDATED_ACCOUNT_TYPE")

        // Get all the bankAccountList where accountType equals to UPDATED_ACCOUNT_TYPE
        defaultBankAccountShouldNotBeFound("accountType.in=$UPDATED_ACCOUNT_TYPE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByAccountTypeIsNullOrNotNull() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        // Get all the bankAccountList where accountType is not null
        defaultBankAccountShouldBeFound("accountType.specified=true")

        // Get all the bankAccountList where accountType is null
        defaultBankAccountShouldNotBeFound("accountType.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByUserIsEqualToSomething() {
        // Initialize the database
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()
        bankAccount.user = user
        bankAccountRepository.saveAndFlush(bankAccount)
        val userId = user.id

        // Get all the bankAccountList where user equals to userId
        defaultBankAccountShouldBeFound("userId.equals=$userId")

        // Get all the bankAccountList where user equals to userId + 1
        defaultBankAccountShouldNotBeFound("userId.equals=${userId?.plus(1)}")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllBankAccountsByOperationIsEqualToSomething() {
        // Initialize the database
        val operation = OperationResourceIT.createEntity(em)
        em.persist(operation)
        em.flush()
        bankAccount.addOperation(operation)
        bankAccountRepository.saveAndFlush(bankAccount)
        val operationId = operation.id

        // Get all the bankAccountList where operation equals to operationId
        defaultBankAccountShouldBeFound("operationId.equals=$operationId")

        // Get all the bankAccountList where operation equals to operationId + 1
        defaultBankAccountShouldNotBeFound("operationId.equals=${operationId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    @Throws(Exception::class)
    private fun defaultBankAccountShouldBeFound(filter: String) {
        restBankAccountMockMvc.perform(get("/api/bank-accounts?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].bankNumber").value(hasItem(DEFAULT_BANK_NUMBER)))
            .andExpect(jsonPath("$.[*].agencyNumber").value(hasItem(DEFAULT_AGENCY_NUMBER?.toInt())))
            .andExpect(jsonPath("$.[*].lastOperationDuration").value(hasItem(DEFAULT_LAST_OPERATION_DURATION.toDouble())))
            .andExpect(jsonPath("$.[*].meanOperationDuration").value(hasItem(DEFAULT_MEAN_OPERATION_DURATION)))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE?.toInt())))
            .andExpect(jsonPath("$.[*].openingDay").value(hasItem(DEFAULT_OPENING_DAY.toString())))
            .andExpect(jsonPath("$.[*].lastOperationDate").value(hasItem(DEFAULT_LAST_OPERATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].attachmentContentType").value(hasItem(DEFAULT_ATTACHMENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].attachment").value(hasItem(Base64Utils.encodeToString(DEFAULT_ATTACHMENT))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))

        // Check, that the count call also returns 1
        restBankAccountMockMvc.perform(get("/api/bank-accounts/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultBankAccountShouldNotBeFound(filter: String) {
        restBankAccountMockMvc.perform(get("/api/bank-accounts?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restBankAccountMockMvc.perform(get("/api/bank-accounts/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingBankAccount() {
        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateBankAccount() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        val databaseSizeBeforeUpdate = bankAccountRepository.findAll().size

        // Update the bankAccount
        val id = bankAccount.id
        assertNotNull(id)
        val updatedBankAccount = bankAccountRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedBankAccount are not directly saved in db
        em.detach(updatedBankAccount)
        updatedBankAccount.name = UPDATED_NAME
        updatedBankAccount.bankNumber = UPDATED_BANK_NUMBER
        updatedBankAccount.agencyNumber = UPDATED_AGENCY_NUMBER
        updatedBankAccount.lastOperationDuration = UPDATED_LAST_OPERATION_DURATION
        updatedBankAccount.meanOperationDuration = UPDATED_MEAN_OPERATION_DURATION
        updatedBankAccount.balance = UPDATED_BALANCE
        updatedBankAccount.openingDay = UPDATED_OPENING_DAY
        updatedBankAccount.lastOperationDate = UPDATED_LAST_OPERATION_DATE
        updatedBankAccount.active = UPDATED_ACTIVE
        updatedBankAccount.accountType = UPDATED_ACCOUNT_TYPE
        updatedBankAccount.attachment = UPDATED_ATTACHMENT
        updatedBankAccount.attachmentContentType = UPDATED_ATTACHMENT_CONTENT_TYPE
        updatedBankAccount.description = UPDATED_DESCRIPTION
        val bankAccountDTO = bankAccountMapper.toDto(updatedBankAccount)

        restBankAccountMockMvc.perform(
            put("/api/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bankAccountDTO))
        ).andExpect(status().isOk)

        // Validate the BankAccount in the database
        val bankAccountList = bankAccountRepository.findAll()
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate)
        val testBankAccount = bankAccountList[bankAccountList.size - 1]
        assertThat(testBankAccount.name).isEqualTo(UPDATED_NAME)
        assertThat(testBankAccount.bankNumber).isEqualTo(UPDATED_BANK_NUMBER)
        assertThat(testBankAccount.agencyNumber).isEqualTo(UPDATED_AGENCY_NUMBER)
        assertThat(testBankAccount.lastOperationDuration).isEqualTo(UPDATED_LAST_OPERATION_DURATION)
        assertThat(testBankAccount.meanOperationDuration).isEqualTo(UPDATED_MEAN_OPERATION_DURATION)
        assertThat(testBankAccount.balance).isEqualTo(UPDATED_BALANCE)
        assertThat(testBankAccount.openingDay).isEqualTo(UPDATED_OPENING_DAY)
        assertThat(testBankAccount.lastOperationDate).isEqualTo(UPDATED_LAST_OPERATION_DATE)
        assertThat(testBankAccount.active).isEqualTo(UPDATED_ACTIVE)
        assertThat(testBankAccount.accountType).isEqualTo(UPDATED_ACCOUNT_TYPE)
        assertThat(testBankAccount.attachment).isEqualTo(UPDATED_ATTACHMENT)
        assertThat(testBankAccount.attachmentContentType).isEqualTo(UPDATED_ATTACHMENT_CONTENT_TYPE)
        assertThat(testBankAccount.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun updateNonExistingBankAccount() {
        val databaseSizeBeforeUpdate = bankAccountRepository.findAll().size

        // Create the BankAccount
        val bankAccountDTO = bankAccountMapper.toDto(bankAccount)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankAccountMockMvc.perform(
            put("/api/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(bankAccountDTO))
        ).andExpect(status().isBadRequest)

        // Validate the BankAccount in the database
        val bankAccountList = bankAccountRepository.findAll()
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteBankAccount() {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount)

        val databaseSizeBeforeDelete = bankAccountRepository.findAll().size

        // Delete the bankAccount
        restBankAccountMockMvc.perform(
            delete("/api/bank-accounts/{id}", bankAccount.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val bankAccountList = bankAccountRepository.findAll()
        assertThat(bankAccountList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_BANK_NUMBER: Int = 1
        private const val UPDATED_BANK_NUMBER: Int = 2
        private const val SMALLER_BANK_NUMBER: Int = 1 - 1

        private const val DEFAULT_AGENCY_NUMBER: Long = 1L
        private const val UPDATED_AGENCY_NUMBER: Long = 2L
        private const val SMALLER_AGENCY_NUMBER: Long = 1L - 1L

        private const val DEFAULT_LAST_OPERATION_DURATION: Float = 1F
        private const val UPDATED_LAST_OPERATION_DURATION: Float = 2F
        private const val SMALLER_LAST_OPERATION_DURATION: Float = 1F - 1F

        private const val DEFAULT_MEAN_OPERATION_DURATION: Double = 1.0
        private const val UPDATED_MEAN_OPERATION_DURATION: Double = 2.0
        private const val SMALLER_MEAN_OPERATION_DURATION: Double = 1.0 - 1.0

        private val DEFAULT_BALANCE: BigDecimal = BigDecimal(1)
        private val UPDATED_BALANCE: BigDecimal = BigDecimal(2)
        private val SMALLER_BALANCE: BigDecimal = BigDecimal(1 - 1)

        private val DEFAULT_OPENING_DAY: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_OPENING_DAY: LocalDate = LocalDate.now(ZoneId.systemDefault())
        private val SMALLER_OPENING_DAY: LocalDate = LocalDate.ofEpochDay(-1L)

        private val DEFAULT_LAST_OPERATION_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_LAST_OPERATION_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_ACTIVE: Boolean = false
        private const val UPDATED_ACTIVE: Boolean = true

        private val DEFAULT_ACCOUNT_TYPE: BankAccountType = BankAccountType.CHECKING
        private val UPDATED_ACCOUNT_TYPE: BankAccountType = BankAccountType.SAVINGS

        private val DEFAULT_ATTACHMENT: ByteArray = createByteArray(1, "0")
        private val UPDATED_ATTACHMENT: ByteArray = createByteArray(1, "1")
        private const val DEFAULT_ATTACHMENT_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_ATTACHMENT_CONTENT_TYPE: String = "image/png"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): BankAccount {
            val bankAccount = BankAccount(
                name = DEFAULT_NAME,
                bankNumber = DEFAULT_BANK_NUMBER,
                agencyNumber = DEFAULT_AGENCY_NUMBER,
                lastOperationDuration = DEFAULT_LAST_OPERATION_DURATION,
                meanOperationDuration = DEFAULT_MEAN_OPERATION_DURATION,
                balance = DEFAULT_BALANCE,
                openingDay = DEFAULT_OPENING_DAY,
                lastOperationDate = DEFAULT_LAST_OPERATION_DATE,
                active = DEFAULT_ACTIVE,
                accountType = DEFAULT_ACCOUNT_TYPE,
                attachment = DEFAULT_ATTACHMENT,
                attachmentContentType = DEFAULT_ATTACHMENT_CONTENT_TYPE,
                description = DEFAULT_DESCRIPTION
            )

            return bankAccount
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): BankAccount {
            val bankAccount = BankAccount(
                name = UPDATED_NAME,
                bankNumber = UPDATED_BANK_NUMBER,
                agencyNumber = UPDATED_AGENCY_NUMBER,
                lastOperationDuration = UPDATED_LAST_OPERATION_DURATION,
                meanOperationDuration = UPDATED_MEAN_OPERATION_DURATION,
                balance = UPDATED_BALANCE,
                openingDay = UPDATED_OPENING_DAY,
                lastOperationDate = UPDATED_LAST_OPERATION_DATE,
                active = UPDATED_ACTIVE,
                accountType = UPDATED_ACCOUNT_TYPE,
                attachment = UPDATED_ATTACHMENT,
                attachmentContentType = UPDATED_ATTACHMENT_CONTENT_TYPE,
                description = UPDATED_DESCRIPTION
            )

            return bankAccount
        }
    }
}
