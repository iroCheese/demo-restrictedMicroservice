package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.RestrictedEntity;
import com.mycompany.myapp.repository.RestrictedEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link RestrictedEntity}.
 */
@Service
@Transactional
public class RestrictedEntityService {

    private final Logger log = LoggerFactory.getLogger(RestrictedEntityService.class);

    private final RestrictedEntityRepository restrictedEntityRepository;

    public RestrictedEntityService(RestrictedEntityRepository restrictedEntityRepository) {
        this.restrictedEntityRepository = restrictedEntityRepository;
    }

    /**
     * Save a restrictedEntity.
     *
     * @param restrictedEntity the entity to save.
     * @return the persisted entity.
     */
    public RestrictedEntity save(RestrictedEntity restrictedEntity) {
        log.debug("Request to save RestrictedEntity : {}", restrictedEntity);
        return restrictedEntityRepository.save(restrictedEntity);
    }

    /**
     * Get all the restrictedEntities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RestrictedEntity> findAll() {
        log.debug("Request to get all RestrictedEntities");
        return restrictedEntityRepository.findAll();
    }


    /**
     * Get one restrictedEntity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RestrictedEntity> findOne(Long id) {
        log.debug("Request to get RestrictedEntity : {}", id);
        return restrictedEntityRepository.findById(id);
    }

    /**
     * Delete the restrictedEntity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RestrictedEntity : {}", id);
        restrictedEntityRepository.deleteById(id);
    }
}
