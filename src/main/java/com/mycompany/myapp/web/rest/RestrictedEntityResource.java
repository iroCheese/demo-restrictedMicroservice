package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.RestrictedEntity;
import com.mycompany.myapp.service.RestrictedEntityService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.RestrictedEntity}.
 */
@RestController
@RequestMapping("/api")
public class RestrictedEntityResource {

    private final Logger log = LoggerFactory.getLogger(RestrictedEntityResource.class);

    private static final String ENTITY_NAME = "restrictedMicroserviceRestrictedEntity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestrictedEntityService restrictedEntityService;

    public RestrictedEntityResource(RestrictedEntityService restrictedEntityService) {
        this.restrictedEntityService = restrictedEntityService;
    }

    /**
     * {@code POST  /restricted-entities} : Create a new restrictedEntity.
     *
     * @param restrictedEntity the restrictedEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restrictedEntity, or with status {@code 400 (Bad Request)} if the restrictedEntity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/restricted-entities")
    public ResponseEntity<RestrictedEntity> createRestrictedEntity(@RequestBody RestrictedEntity restrictedEntity) throws URISyntaxException {
        log.debug("REST request to save RestrictedEntity : {}", restrictedEntity);
        if (restrictedEntity.getId() != null) {
            throw new BadRequestAlertException("A new restrictedEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RestrictedEntity result = restrictedEntityService.save(restrictedEntity);
        return ResponseEntity.created(new URI("/api/restricted-entities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /restricted-entities} : Updates an existing restrictedEntity.
     *
     * @param restrictedEntity the restrictedEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restrictedEntity,
     * or with status {@code 400 (Bad Request)} if the restrictedEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restrictedEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/restricted-entities")
    public ResponseEntity<RestrictedEntity> updateRestrictedEntity(@RequestBody RestrictedEntity restrictedEntity) throws URISyntaxException {
        log.debug("REST request to update RestrictedEntity : {}", restrictedEntity);
        if (restrictedEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RestrictedEntity result = restrictedEntityService.save(restrictedEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, restrictedEntity.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /restricted-entities} : get all the restrictedEntities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restrictedEntities in body.
     */
    @GetMapping("/restricted-entities")
    public List<RestrictedEntity> getAllRestrictedEntities() {
        log.debug("REST request to get all RestrictedEntities");
        return restrictedEntityService.findAll();
    }

    /**
     * {@code GET  /restricted-entities/:id} : get the "id" restrictedEntity.
     *
     * @param id the id of the restrictedEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restrictedEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/restricted-entities/{id}")
    public ResponseEntity<RestrictedEntity> getRestrictedEntity(@PathVariable Long id) {
        log.debug("REST request to get RestrictedEntity : {}", id);
        Optional<RestrictedEntity> restrictedEntity = restrictedEntityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restrictedEntity);
    }

    /**
     * {@code DELETE  /restricted-entities/:id} : delete the "id" restrictedEntity.
     *
     * @param id the id of the restrictedEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/restricted-entities/{id}")
    public ResponseEntity<Void> deleteRestrictedEntity(@PathVariable Long id) {
        log.debug("REST request to delete RestrictedEntity : {}", id);
        restrictedEntityService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
