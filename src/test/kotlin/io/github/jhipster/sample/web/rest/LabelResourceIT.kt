package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.domain.Label
import io.github.jhipster.sample.repository.LabelRepository
import io.github.jhipster.sample.service.LabelService
import io.github.jhipster.sample.web.rest.errors.ExceptionTranslator
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
import org.springframework.validation.Validator

/**
 * Integration tests for the [LabelResource] REST controller.
 *
 * @see LabelResource
 */
@SpringBootTest(classes = [JhipsterApp::class])
@AutoConfigureMockMvc
@WithMockUser
class LabelResourceIT {

    @Autowired
    private lateinit var labelRepository: LabelRepository

    @Autowired
    private lateinit var labelService: LabelService

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

    private lateinit var restLabelMockMvc: MockMvc

    private lateinit var label: Label

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val labelResource = LabelResource(labelService)
         this.restLabelMockMvc = MockMvcBuilders.standaloneSetup(labelResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        label = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createLabel() {
        val databaseSizeBeforeCreate = labelRepository.findAll().size

        // Create the Label
        restLabelMockMvc.perform(
            post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(label))
        ).andExpect(status().isCreated)

        // Validate the Label in the database
        val labelList = labelRepository.findAll()
        assertThat(labelList).hasSize(databaseSizeBeforeCreate + 1)
        val testLabel = labelList[labelList.size - 1]
        assertThat(testLabel.labelName).isEqualTo(DEFAULT_LABEL_NAME)
    }

    @Test
    @Transactional
    fun createLabelWithExistingId() {
        val databaseSizeBeforeCreate = labelRepository.findAll().size

        // Create the Label with an existing ID
        label.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restLabelMockMvc.perform(
            post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(label))
        ).andExpect(status().isBadRequest)

        // Validate the Label in the database
        val labelList = labelRepository.findAll()
        assertThat(labelList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkLabelNameIsRequired() {
        val databaseSizeBeforeTest = labelRepository.findAll().size
        // set the field null
        label.labelName = null

        // Create the Label, which fails.

        restLabelMockMvc.perform(
            post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(label))
        ).andExpect(status().isBadRequest)

        val labelList = labelRepository.findAll()
        assertThat(labelList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllLabels() {
        // Initialize the database
        labelRepository.saveAndFlush(label)

        // Get all the labelList
        restLabelMockMvc.perform(get("/api/labels?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(label.id?.toInt())))
            .andExpect(jsonPath("$.[*].labelName").value(hasItem(DEFAULT_LABEL_NAME))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getLabel() {
        // Initialize the database
        labelRepository.saveAndFlush(label)

        val id = label.id
        assertNotNull(id)

        // Get the label
        restLabelMockMvc.perform(get("/api/labels/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(label.id?.toInt()))
            .andExpect(jsonPath("$.labelName").value(DEFAULT_LABEL_NAME)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingLabel() {
        // Get the label
        restLabelMockMvc.perform(get("/api/labels/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateLabel() {
        // Initialize the database
        labelService.save(label)

        val databaseSizeBeforeUpdate = labelRepository.findAll().size

        // Update the label
        val id = label.id
        assertNotNull(id)
        val updatedLabel = labelRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedLabel are not directly saved in db
        em.detach(updatedLabel)
        updatedLabel.labelName = UPDATED_LABEL_NAME

        restLabelMockMvc.perform(
            put("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedLabel))
        ).andExpect(status().isOk)

        // Validate the Label in the database
        val labelList = labelRepository.findAll()
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate)
        val testLabel = labelList[labelList.size - 1]
        assertThat(testLabel.labelName).isEqualTo(UPDATED_LABEL_NAME)
    }

    @Test
    @Transactional
    fun updateNonExistingLabel() {
        val databaseSizeBeforeUpdate = labelRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLabelMockMvc.perform(
            put("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(label))
        ).andExpect(status().isBadRequest)

        // Validate the Label in the database
        val labelList = labelRepository.findAll()
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteLabel() {
        // Initialize the database
        labelService.save(label)

        val databaseSizeBeforeDelete = labelRepository.findAll().size

        // Delete the label
        restLabelMockMvc.perform(
            delete("/api/labels/{id}", label.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val labelList = labelRepository.findAll()
        assertThat(labelList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_LABEL_NAME = "AAAAAAAAAA"
        private const val UPDATED_LABEL_NAME = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Label {
            val label = Label(
                labelName = DEFAULT_LABEL_NAME
            )

            return label
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Label {
            val label = Label(
                labelName = UPDATED_LABEL_NAME
            )

            return label
        }
    }
}
