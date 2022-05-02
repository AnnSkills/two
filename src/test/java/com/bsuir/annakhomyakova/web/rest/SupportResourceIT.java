package com.bsuir.annakhomyakova.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bsuir.annakhomyakova.IntegrationTest;
import com.bsuir.annakhomyakova.domain.SupportAnnKh;
import com.bsuir.annakhomyakova.repository.SupportRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SupportResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SupportResourceIT {

    private static final String DEFAULT_TOPIC = "AAAAAAAAAA";
    private static final String UPDATED_TOPIC = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "LB@\\S";
    private static final String UPDATED_EMAIL = "`<@@\\SSSS";

    private static final String DEFAULT_PHONE = "\\\\\\\\\\2168 96 9808 345 97";
    private static final String UPDATED_PHONE = "\\\\\\\\\\68541 2 570234";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/supports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SupportRepository supportRepository;

    @Mock
    private SupportRepository supportRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupportMockMvc;

    private SupportAnnKh supportAnnKh;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupportAnnKh createEntity(EntityManager em) {
        SupportAnnKh supportAnnKh = new SupportAnnKh()
            .topic(DEFAULT_TOPIC)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .description(DEFAULT_DESCRIPTION);
        return supportAnnKh;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupportAnnKh createUpdatedEntity(EntityManager em) {
        SupportAnnKh supportAnnKh = new SupportAnnKh()
            .topic(UPDATED_TOPIC)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .description(UPDATED_DESCRIPTION);
        return supportAnnKh;
    }

    @BeforeEach
    public void initTest() {
        supportAnnKh = createEntity(em);
    }

    @Test
    @Transactional
    void createSupport() throws Exception {
        int databaseSizeBeforeCreate = supportRepository.findAll().size();
        // Create the Support
        restSupportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isCreated());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeCreate + 1);
        SupportAnnKh testSupport = supportList.get(supportList.size() - 1);
        assertThat(testSupport.getTopic()).isEqualTo(DEFAULT_TOPIC);
        assertThat(testSupport.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testSupport.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testSupport.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSupportWithExistingId() throws Exception {
        // Create the Support with an existing ID
        supportAnnKh.setId(1L);

        int databaseSizeBeforeCreate = supportRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = supportRepository.findAll().size();
        // set the field null
        supportAnnKh.setEmail(null);

        // Create the Support, which fails.

        restSupportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isBadRequest());

        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = supportRepository.findAll().size();
        // set the field null
        supportAnnKh.setPhone(null);

        // Create the Support, which fails.

        restSupportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isBadRequest());

        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSupports() throws Exception {
        // Initialize the database
        supportRepository.saveAndFlush(supportAnnKh);

        // Get all the supportList
        restSupportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supportAnnKh.getId().intValue())))
            .andExpect(jsonPath("$.[*].topic").value(hasItem(DEFAULT_TOPIC)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupportsWithEagerRelationshipsIsEnabled() throws Exception {
        when(supportRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(supportRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupportsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(supportRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(supportRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getSupport() throws Exception {
        // Initialize the database
        supportRepository.saveAndFlush(supportAnnKh);

        // Get the support
        restSupportMockMvc
            .perform(get(ENTITY_API_URL_ID, supportAnnKh.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supportAnnKh.getId().intValue()))
            .andExpect(jsonPath("$.topic").value(DEFAULT_TOPIC))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingSupport() throws Exception {
        // Get the support
        restSupportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSupport() throws Exception {
        // Initialize the database
        supportRepository.saveAndFlush(supportAnnKh);

        int databaseSizeBeforeUpdate = supportRepository.findAll().size();

        // Update the support
        SupportAnnKh updatedSupportAnnKh = supportRepository.findById(supportAnnKh.getId()).get();
        // Disconnect from session so that the updates on updatedSupportAnnKh are not directly saved in db
        em.detach(updatedSupportAnnKh);
        updatedSupportAnnKh.topic(UPDATED_TOPIC).email(UPDATED_EMAIL).phone(UPDATED_PHONE).description(UPDATED_DESCRIPTION);

        restSupportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSupportAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSupportAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
        SupportAnnKh testSupport = supportList.get(supportList.size() - 1);
        assertThat(testSupport.getTopic()).isEqualTo(UPDATED_TOPIC);
        assertThat(testSupport.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testSupport.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testSupport.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingSupport() throws Exception {
        int databaseSizeBeforeUpdate = supportRepository.findAll().size();
        supportAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supportAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupport() throws Exception {
        int databaseSizeBeforeUpdate = supportRepository.findAll().size();
        supportAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupport() throws Exception {
        int databaseSizeBeforeUpdate = supportRepository.findAll().size();
        supportAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSupportWithPatch() throws Exception {
        // Initialize the database
        supportRepository.saveAndFlush(supportAnnKh);

        int databaseSizeBeforeUpdate = supportRepository.findAll().size();

        // Update the support using partial update
        SupportAnnKh partialUpdatedSupportAnnKh = new SupportAnnKh();
        partialUpdatedSupportAnnKh.setId(supportAnnKh.getId());

        partialUpdatedSupportAnnKh.description(UPDATED_DESCRIPTION);

        restSupportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupportAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupportAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
        SupportAnnKh testSupport = supportList.get(supportList.size() - 1);
        assertThat(testSupport.getTopic()).isEqualTo(DEFAULT_TOPIC);
        assertThat(testSupport.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testSupport.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testSupport.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSupportWithPatch() throws Exception {
        // Initialize the database
        supportRepository.saveAndFlush(supportAnnKh);

        int databaseSizeBeforeUpdate = supportRepository.findAll().size();

        // Update the support using partial update
        SupportAnnKh partialUpdatedSupportAnnKh = new SupportAnnKh();
        partialUpdatedSupportAnnKh.setId(supportAnnKh.getId());

        partialUpdatedSupportAnnKh.topic(UPDATED_TOPIC).email(UPDATED_EMAIL).phone(UPDATED_PHONE).description(UPDATED_DESCRIPTION);

        restSupportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupportAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupportAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
        SupportAnnKh testSupport = supportList.get(supportList.size() - 1);
        assertThat(testSupport.getTopic()).isEqualTo(UPDATED_TOPIC);
        assertThat(testSupport.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testSupport.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testSupport.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSupport() throws Exception {
        int databaseSizeBeforeUpdate = supportRepository.findAll().size();
        supportAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supportAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupport() throws Exception {
        int databaseSizeBeforeUpdate = supportRepository.findAll().size();
        supportAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupport() throws Exception {
        int databaseSizeBeforeUpdate = supportRepository.findAll().size();
        supportAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupportMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supportAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Support in the database
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSupport() throws Exception {
        // Initialize the database
        supportRepository.saveAndFlush(supportAnnKh);

        int databaseSizeBeforeDelete = supportRepository.findAll().size();

        // Delete the support
        restSupportMockMvc
            .perform(delete(ENTITY_API_URL_ID, supportAnnKh.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SupportAnnKh> supportList = supportRepository.findAll();
        assertThat(supportList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
