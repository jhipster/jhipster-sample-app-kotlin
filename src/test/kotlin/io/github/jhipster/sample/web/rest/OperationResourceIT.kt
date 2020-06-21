package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.domain.Operation
import io.github.jhipster.sample.repository.OperationRepository
import io.github.jhipster.sample.web.rest.errors.ExceptionTranslator
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
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
import org.springframework.validation.Validator

/**
 * Integration tests for the [OperationResource] REST controller.
 *
 * @see OperationResource
 */
@SpringBootTest(classes = [JhipsterApp::class])
@AutoConfigureMockMvc
@WithMockUser
@Extensions(
    ExtendWith(MockitoExtension::class)
)
class OperationResourceIT {

    @Autowired
    private lateinit var operationRepository: OperationRepository

    @Mock
    private lateinit var operationRepositoryMock: OperationRepository

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

    private lateinit var restOperationMockMvc: MockMvc

    private lateinit var operation: Operation

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val operationResource = OperationResource(operationRepository)
         this.restOperationMockMvc = MockMvcBuilders.standaloneSetup(operationResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        operation = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createOperation() {
        val databaseSizeBeforeCreate = operationRepository.findAll().size

        // Create the Operation
        restOperationMockMvc.perform(
            post("/api/operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(operation))
        ).andExpect(status().isCreated)

        // Validate the Operation in the database
        val operationList = operationRepository.findAll()
        assertThat(operationList).hasSize(databaseSizeBeforeCreate + 1)
        val testOperation = operationList[operationList.size - 1]
        assertThat(testOperation.date).isEqualTo(DEFAULT_DATE)
        assertThat(testOperation.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testOperation.amount).isEqualTo(DEFAULT_AMOUNT)
    }

    @Test
    @Transactional
    fun createOperationWithExistingId() {
        val databaseSizeBeforeCreate = operationRepository.findAll().size

        // Create the Operation with an existing ID
        operation.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperationMockMvc.perform(
            post("/api/operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(operation))
        ).andExpect(status().isBadRequest)

        // Validate the Operation in the database
        val operationList = operationRepository.findAll()
        assertThat(operationList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = operationRepository.findAll().size
        // set the field null
        operation.date = null

        // Create the Operation, which fails.

        restOperationMockMvc.perform(
            post("/api/operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(operation))
        ).andExpect(status().isBadRequest)

        val operationList = operationRepository.findAll()
        assertThat(operationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkAmountIsRequired() {
        val databaseSizeBeforeTest = operationRepository.findAll().size
        // set the field null
        operation.amount = null

        // Create the Operation, which fails.

        restOperationMockMvc.perform(
            post("/api/operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(operation))
        ).andExpect(status().isBadRequest)

        val operationList = operationRepository.findAll()
        assertThat(operationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllOperations() {
        // Initialize the database
        operationRepository.saveAndFlush(operation)

        // Get all the operationList
        restOperationMockMvc.perform(get("/api/operations?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operation.id?.toInt())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT?.toInt()))) }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllOperationsWithEagerRelationshipsIsEnabled() {
        val operationResource = OperationResource(operationRepositoryMock)
        `when`(operationRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restOperationMockMvc = MockMvcBuilders.standaloneSetup(operationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restOperationMockMvc.perform(get("/api/operations?eagerload=true"))
            .andExpect(status().isOk)

        verify(operationRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllOperationsWithEagerRelationshipsIsNotEnabled() {
            val operationResource = OperationResource(operationRepositoryMock)
        `when`(operationRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restOperationMockMvc = MockMvcBuilders.standaloneSetup(operationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restOperationMockMvc.perform(get("/api/operations?eagerload=true"))
            .andExpect(status().isOk)

        verify(operationRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getOperation() {
        // Initialize the database
        operationRepository.saveAndFlush(operation)

        val id = operation.id
        assertNotNull(id)

        // Get the operation
        restOperationMockMvc.perform(get("/api/operations/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(operation.id?.toInt()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT?.toInt())) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingOperation() {
        // Get the operation
        restOperationMockMvc.perform(get("/api/operations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateOperation() {
        // Initialize the database
        operationRepository.saveAndFlush(operation)

        val databaseSizeBeforeUpdate = operationRepository.findAll().size

        // Update the operation
        val id = operation.id
        assertNotNull(id)
        val updatedOperation = operationRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOperation are not directly saved in db
        em.detach(updatedOperation)
        updatedOperation.date = UPDATED_DATE
        updatedOperation.description = UPDATED_DESCRIPTION
        updatedOperation.amount = UPDATED_AMOUNT

        restOperationMockMvc.perform(
            put("/api/operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedOperation))
        ).andExpect(status().isOk)

        // Validate the Operation in the database
        val operationList = operationRepository.findAll()
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate)
        val testOperation = operationList[operationList.size - 1]
        assertThat(testOperation.date).isEqualTo(UPDATED_DATE)
        assertThat(testOperation.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testOperation.amount).isEqualTo(UPDATED_AMOUNT)
    }

    @Test
    @Transactional
    fun updateNonExistingOperation() {
        val databaseSizeBeforeUpdate = operationRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationMockMvc.perform(
            put("/api/operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(operation))
        ).andExpect(status().isBadRequest)

        // Validate the Operation in the database
        val operationList = operationRepository.findAll()
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteOperation() {
        // Initialize the database
        operationRepository.saveAndFlush(operation)

        val databaseSizeBeforeDelete = operationRepository.findAll().size

        // Delete the operation
        restOperationMockMvc.perform(
            delete("/api/operations/{id}", operation.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val operationList = operationRepository.findAll()
        assertThat(operationList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private val DEFAULT_AMOUNT: BigDecimal = BigDecimal(1)
        private val UPDATED_AMOUNT: BigDecimal = BigDecimal(2)

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Operation {
            val operation = Operation(
                date = DEFAULT_DATE,
                description = DEFAULT_DESCRIPTION,
                amount = DEFAULT_AMOUNT
            )

            return operation
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Operation {
            val operation = Operation(
                date = UPDATED_DATE,
                description = UPDATED_DESCRIPTION,
                amount = UPDATED_AMOUNT
            )

            return operation
        }
    }
}
