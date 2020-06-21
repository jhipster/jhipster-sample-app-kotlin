package io.github.jhipster.sample.service
import io.github.jhipster.sample.domain.Label
import io.github.jhipster.sample.repository.LabelRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Label].
 */
@Service
@Transactional
class LabelService(
    private val labelRepository: LabelRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a label.
     *
     * @param label the entity to save.
     * @return the persisted entity.
     */
    fun save(label: Label): Label {
        log.debug("Request to save Label : {}", label)
        return labelRepository.save(label)
    }

    /**
     * Get all the labels.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Label> {
        log.debug("Request to get all Labels")
        return labelRepository.findAll(pageable)
    }

    /**
     * Get one label by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Label> {
        log.debug("Request to get Label : {}", id)
        return labelRepository.findById(id)
    }

    /**
     * Delete the label by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Label : {}", id)

        labelRepository.deleteById(id)
    }
}
