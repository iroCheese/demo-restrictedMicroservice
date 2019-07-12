package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.RestrictedMicroserviceApp;
import com.mycompany.myapp.config.SecurityBeanOverrideConfiguration;
import com.mycompany.myapp.domain.RestrictedEntity;
import com.mycompany.myapp.repository.RestrictedEntityRepository;
import com.mycompany.myapp.service.RestrictedEntityService;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link RestrictedEntityResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, RestrictedMicroserviceApp.class})
public class RestrictedEntityResourceIT {

    private static final String DEFAULT_MY_FIELD = "AAAAAAAAAA";
    private static final String UPDATED_MY_FIELD = "BBBBBBBBBB";

    @Autowired
    private RestrictedEntityRepository restrictedEntityRepository;

    @Autowired
    private RestrictedEntityService restrictedEntityService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restRestrictedEntityMockMvc;

    private RestrictedEntity restrictedEntity;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RestrictedEntityResource restrictedEntityResource = new RestrictedEntityResource(restrictedEntityService);
        this.restRestrictedEntityMockMvc = MockMvcBuilders.standaloneSetup(restrictedEntityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestrictedEntity createEntity(EntityManager em) {
        RestrictedEntity restrictedEntity = new RestrictedEntity()
            .myField(DEFAULT_MY_FIELD);
        return restrictedEntity;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestrictedEntity createUpdatedEntity(EntityManager em) {
        RestrictedEntity restrictedEntity = new RestrictedEntity()
            .myField(UPDATED_MY_FIELD);
        return restrictedEntity;
    }

    @BeforeEach
    public void initTest() {
        restrictedEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createRestrictedEntity() throws Exception {
        int databaseSizeBeforeCreate = restrictedEntityRepository.findAll().size();

        // Create the RestrictedEntity
        restRestrictedEntityMockMvc.perform(post("/api/restricted-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restrictedEntity)))
            .andExpect(status().isCreated());

        // Validate the RestrictedEntity in the database
        List<RestrictedEntity> restrictedEntityList = restrictedEntityRepository.findAll();
        assertThat(restrictedEntityList).hasSize(databaseSizeBeforeCreate + 1);
        RestrictedEntity testRestrictedEntity = restrictedEntityList.get(restrictedEntityList.size() - 1);
        assertThat(testRestrictedEntity.getMyField()).isEqualTo(DEFAULT_MY_FIELD);
    }

    @Test
    @Transactional
    public void createRestrictedEntityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = restrictedEntityRepository.findAll().size();

        // Create the RestrictedEntity with an existing ID
        restrictedEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestrictedEntityMockMvc.perform(post("/api/restricted-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restrictedEntity)))
            .andExpect(status().isBadRequest());

        // Validate the RestrictedEntity in the database
        List<RestrictedEntity> restrictedEntityList = restrictedEntityRepository.findAll();
        assertThat(restrictedEntityList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllRestrictedEntities() throws Exception {
        // Initialize the database
        restrictedEntityRepository.saveAndFlush(restrictedEntity);

        // Get all the restrictedEntityList
        restRestrictedEntityMockMvc.perform(get("/api/restricted-entities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restrictedEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].myField").value(hasItem(DEFAULT_MY_FIELD.toString())));
    }
    
    @Test
    @Transactional
    public void getRestrictedEntity() throws Exception {
        // Initialize the database
        restrictedEntityRepository.saveAndFlush(restrictedEntity);

        // Get the restrictedEntity
        restRestrictedEntityMockMvc.perform(get("/api/restricted-entities/{id}", restrictedEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(restrictedEntity.getId().intValue()))
            .andExpect(jsonPath("$.myField").value(DEFAULT_MY_FIELD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRestrictedEntity() throws Exception {
        // Get the restrictedEntity
        restRestrictedEntityMockMvc.perform(get("/api/restricted-entities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRestrictedEntity() throws Exception {
        // Initialize the database
        restrictedEntityService.save(restrictedEntity);

        int databaseSizeBeforeUpdate = restrictedEntityRepository.findAll().size();

        // Update the restrictedEntity
        RestrictedEntity updatedRestrictedEntity = restrictedEntityRepository.findById(restrictedEntity.getId()).get();
        // Disconnect from session so that the updates on updatedRestrictedEntity are not directly saved in db
        em.detach(updatedRestrictedEntity);
        updatedRestrictedEntity
            .myField(UPDATED_MY_FIELD);

        restRestrictedEntityMockMvc.perform(put("/api/restricted-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRestrictedEntity)))
            .andExpect(status().isOk());

        // Validate the RestrictedEntity in the database
        List<RestrictedEntity> restrictedEntityList = restrictedEntityRepository.findAll();
        assertThat(restrictedEntityList).hasSize(databaseSizeBeforeUpdate);
        RestrictedEntity testRestrictedEntity = restrictedEntityList.get(restrictedEntityList.size() - 1);
        assertThat(testRestrictedEntity.getMyField()).isEqualTo(UPDATED_MY_FIELD);
    }

    @Test
    @Transactional
    public void updateNonExistingRestrictedEntity() throws Exception {
        int databaseSizeBeforeUpdate = restrictedEntityRepository.findAll().size();

        // Create the RestrictedEntity

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestrictedEntityMockMvc.perform(put("/api/restricted-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restrictedEntity)))
            .andExpect(status().isBadRequest());

        // Validate the RestrictedEntity in the database
        List<RestrictedEntity> restrictedEntityList = restrictedEntityRepository.findAll();
        assertThat(restrictedEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRestrictedEntity() throws Exception {
        // Initialize the database
        restrictedEntityService.save(restrictedEntity);

        int databaseSizeBeforeDelete = restrictedEntityRepository.findAll().size();

        // Delete the restrictedEntity
        restRestrictedEntityMockMvc.perform(delete("/api/restricted-entities/{id}", restrictedEntity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<RestrictedEntity> restrictedEntityList = restrictedEntityRepository.findAll();
        assertThat(restrictedEntityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestrictedEntity.class);
        RestrictedEntity restrictedEntity1 = new RestrictedEntity();
        restrictedEntity1.setId(1L);
        RestrictedEntity restrictedEntity2 = new RestrictedEntity();
        restrictedEntity2.setId(restrictedEntity1.getId());
        assertThat(restrictedEntity1).isEqualTo(restrictedEntity2);
        restrictedEntity2.setId(2L);
        assertThat(restrictedEntity1).isNotEqualTo(restrictedEntity2);
        restrictedEntity1.setId(null);
        assertThat(restrictedEntity1).isNotEqualTo(restrictedEntity2);
    }
}
