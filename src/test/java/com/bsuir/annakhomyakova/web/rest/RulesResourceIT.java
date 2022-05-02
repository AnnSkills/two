package com.bsuir.annakhomyakova.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bsuir.annakhomyakova.IntegrationTest;
import com.bsuir.annakhomyakova.domain.RulesAnnKh;
import com.bsuir.annakhomyakova.repository.RulesRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link RulesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RulesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_REQUIREMENTS = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_REQUIREMENTS = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_REQUIREMENTS_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_REQUIREMENTS_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/rules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RulesRepository rulesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRulesMockMvc;

    private RulesAnnKh rulesAnnKh;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RulesAnnKh createEntity(EntityManager em) {
        RulesAnnKh rulesAnnKh = new RulesAnnKh()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .requirements(DEFAULT_REQUIREMENTS)
            .requirementsContentType(DEFAULT_REQUIREMENTS_CONTENT_TYPE);
        return rulesAnnKh;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RulesAnnKh createUpdatedEntity(EntityManager em) {
        RulesAnnKh rulesAnnKh = new RulesAnnKh()
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .requirements(UPDATED_REQUIREMENTS)
            .requirementsContentType(UPDATED_REQUIREMENTS_CONTENT_TYPE);
        return rulesAnnKh;
    }

    @BeforeEach
    public void initTest() {
        rulesAnnKh = createEntity(em);
    }

    @Test
    @Transactional
    void createRules() throws Exception {
        int databaseSizeBeforeCreate = rulesRepository.findAll().size();
        // Create the Rules
        restRulesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isCreated());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeCreate + 1);
        RulesAnnKh testRules = rulesList.get(rulesList.size() - 1);
        assertThat(testRules.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRules.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRules.getRequirements()).isEqualTo(DEFAULT_REQUIREMENTS);
        assertThat(testRules.getRequirementsContentType()).isEqualTo(DEFAULT_REQUIREMENTS_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createRulesWithExistingId() throws Exception {
        // Create the Rules with an existing ID
        rulesAnnKh.setId(1L);

        int databaseSizeBeforeCreate = rulesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRulesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rulesRepository.findAll().size();
        // set the field null
        rulesAnnKh.setName(null);

        // Create the Rules, which fails.

        restRulesMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isBadRequest());

        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRules() throws Exception {
        // Initialize the database
        rulesRepository.saveAndFlush(rulesAnnKh);

        // Get all the rulesList
        restRulesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rulesAnnKh.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].requirementsContentType").value(hasItem(DEFAULT_REQUIREMENTS_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].requirements").value(hasItem(Base64Utils.encodeToString(DEFAULT_REQUIREMENTS))));
    }

    @Test
    @Transactional
    void getRules() throws Exception {
        // Initialize the database
        rulesRepository.saveAndFlush(rulesAnnKh);

        // Get the rules
        restRulesMockMvc
            .perform(get(ENTITY_API_URL_ID, rulesAnnKh.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rulesAnnKh.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.requirementsContentType").value(DEFAULT_REQUIREMENTS_CONTENT_TYPE))
            .andExpect(jsonPath("$.requirements").value(Base64Utils.encodeToString(DEFAULT_REQUIREMENTS)));
    }

    @Test
    @Transactional
    void getNonExistingRules() throws Exception {
        // Get the rules
        restRulesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRules() throws Exception {
        // Initialize the database
        rulesRepository.saveAndFlush(rulesAnnKh);

        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();

        // Update the rules
        RulesAnnKh updatedRulesAnnKh = rulesRepository.findById(rulesAnnKh.getId()).get();
        // Disconnect from session so that the updates on updatedRulesAnnKh are not directly saved in db
        em.detach(updatedRulesAnnKh);
        updatedRulesAnnKh
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .requirements(UPDATED_REQUIREMENTS)
            .requirementsContentType(UPDATED_REQUIREMENTS_CONTENT_TYPE);

        restRulesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRulesAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRulesAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
        RulesAnnKh testRules = rulesList.get(rulesList.size() - 1);
        assertThat(testRules.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRules.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRules.getRequirements()).isEqualTo(UPDATED_REQUIREMENTS);
        assertThat(testRules.getRequirementsContentType()).isEqualTo(UPDATED_REQUIREMENTS_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingRules() throws Exception {
        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();
        rulesAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRulesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rulesAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRules() throws Exception {
        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();
        rulesAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRulesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRules() throws Exception {
        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();
        rulesAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRulesMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRulesWithPatch() throws Exception {
        // Initialize the database
        rulesRepository.saveAndFlush(rulesAnnKh);

        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();

        // Update the rules using partial update
        RulesAnnKh partialUpdatedRulesAnnKh = new RulesAnnKh();
        partialUpdatedRulesAnnKh.setId(rulesAnnKh.getId());

        partialUpdatedRulesAnnKh
            .code(UPDATED_CODE)
            .requirements(UPDATED_REQUIREMENTS)
            .requirementsContentType(UPDATED_REQUIREMENTS_CONTENT_TYPE);

        restRulesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRulesAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRulesAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
        RulesAnnKh testRules = rulesList.get(rulesList.size() - 1);
        assertThat(testRules.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRules.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRules.getRequirements()).isEqualTo(UPDATED_REQUIREMENTS);
        assertThat(testRules.getRequirementsContentType()).isEqualTo(UPDATED_REQUIREMENTS_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateRulesWithPatch() throws Exception {
        // Initialize the database
        rulesRepository.saveAndFlush(rulesAnnKh);

        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();

        // Update the rules using partial update
        RulesAnnKh partialUpdatedRulesAnnKh = new RulesAnnKh();
        partialUpdatedRulesAnnKh.setId(rulesAnnKh.getId());

        partialUpdatedRulesAnnKh
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .requirements(UPDATED_REQUIREMENTS)
            .requirementsContentType(UPDATED_REQUIREMENTS_CONTENT_TYPE);

        restRulesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRulesAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRulesAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
        RulesAnnKh testRules = rulesList.get(rulesList.size() - 1);
        assertThat(testRules.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRules.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRules.getRequirements()).isEqualTo(UPDATED_REQUIREMENTS);
        assertThat(testRules.getRequirementsContentType()).isEqualTo(UPDATED_REQUIREMENTS_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingRules() throws Exception {
        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();
        rulesAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRulesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rulesAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRules() throws Exception {
        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();
        rulesAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRulesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRules() throws Exception {
        int databaseSizeBeforeUpdate = rulesRepository.findAll().size();
        rulesAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRulesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rulesAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rules in the database
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRules() throws Exception {
        // Initialize the database
        rulesRepository.saveAndFlush(rulesAnnKh);

        int databaseSizeBeforeDelete = rulesRepository.findAll().size();

        // Delete the rules
        restRulesMockMvc
            .perform(delete(ENTITY_API_URL_ID, rulesAnnKh.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RulesAnnKh> rulesList = rulesRepository.findAll();
        assertThat(rulesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
