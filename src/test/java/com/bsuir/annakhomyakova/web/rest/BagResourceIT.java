package com.bsuir.annakhomyakova.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bsuir.annakhomyakova.IntegrationTest;
import com.bsuir.annakhomyakova.domain.BagAnnKh;
import com.bsuir.annakhomyakova.domain.enumeration.Status;
import com.bsuir.annakhomyakova.repository.BagRepository;
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
 * Integration tests for the {@link BagResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BagResourceIT {

    private static final String DEFAULT_BAG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BAG_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.OPEN;
    private static final Status UPDATED_STATUS = Status.CLOSED;

    private static final String ENTITY_API_URL = "/api/bags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BagRepository bagRepository;

    @Mock
    private BagRepository bagRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBagMockMvc;

    private BagAnnKh bagAnnKh;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BagAnnKh createEntity(EntityManager em) {
        BagAnnKh bagAnnKh = new BagAnnKh().bagName(DEFAULT_BAG_NAME).description(DEFAULT_DESCRIPTION).status(DEFAULT_STATUS);
        return bagAnnKh;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BagAnnKh createUpdatedEntity(EntityManager em) {
        BagAnnKh bagAnnKh = new BagAnnKh().bagName(UPDATED_BAG_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);
        return bagAnnKh;
    }

    @BeforeEach
    public void initTest() {
        bagAnnKh = createEntity(em);
    }

    @Test
    @Transactional
    void createBag() throws Exception {
        int databaseSizeBeforeCreate = bagRepository.findAll().size();
        // Create the Bag
        restBagMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isCreated());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeCreate + 1);
        BagAnnKh testBag = bagList.get(bagList.size() - 1);
        assertThat(testBag.getBagName()).isEqualTo(DEFAULT_BAG_NAME);
        assertThat(testBag.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBag.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createBagWithExistingId() throws Exception {
        // Create the Bag with an existing ID
        bagAnnKh.setId(1L);

        int databaseSizeBeforeCreate = bagRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBagMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBagNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = bagRepository.findAll().size();
        // set the field null
        bagAnnKh.setBagName(null);

        // Create the Bag, which fails.

        restBagMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isBadRequest());

        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBags() throws Exception {
        // Initialize the database
        bagRepository.saveAndFlush(bagAnnKh);

        // Get all the bagList
        restBagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bagAnnKh.getId().intValue())))
            .andExpect(jsonPath("$.[*].bagName").value(hasItem(DEFAULT_BAG_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBagsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bagRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBagMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bagRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBagsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bagRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBagMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bagRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getBag() throws Exception {
        // Initialize the database
        bagRepository.saveAndFlush(bagAnnKh);

        // Get the bag
        restBagMockMvc
            .perform(get(ENTITY_API_URL_ID, bagAnnKh.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bagAnnKh.getId().intValue()))
            .andExpect(jsonPath("$.bagName").value(DEFAULT_BAG_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBag() throws Exception {
        // Get the bag
        restBagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBag() throws Exception {
        // Initialize the database
        bagRepository.saveAndFlush(bagAnnKh);

        int databaseSizeBeforeUpdate = bagRepository.findAll().size();

        // Update the bag
        BagAnnKh updatedBagAnnKh = bagRepository.findById(bagAnnKh.getId()).get();
        // Disconnect from session so that the updates on updatedBagAnnKh are not directly saved in db
        em.detach(updatedBagAnnKh);
        updatedBagAnnKh.bagName(UPDATED_BAG_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        restBagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBagAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBagAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
        BagAnnKh testBag = bagList.get(bagList.size() - 1);
        assertThat(testBag.getBagName()).isEqualTo(UPDATED_BAG_NAME);
        assertThat(testBag.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBag.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingBag() throws Exception {
        int databaseSizeBeforeUpdate = bagRepository.findAll().size();
        bagAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bagAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBag() throws Exception {
        int databaseSizeBeforeUpdate = bagRepository.findAll().size();
        bagAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBag() throws Exception {
        int databaseSizeBeforeUpdate = bagRepository.findAll().size();
        bagAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBagMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBagWithPatch() throws Exception {
        // Initialize the database
        bagRepository.saveAndFlush(bagAnnKh);

        int databaseSizeBeforeUpdate = bagRepository.findAll().size();

        // Update the bag using partial update
        BagAnnKh partialUpdatedBagAnnKh = new BagAnnKh();
        partialUpdatedBagAnnKh.setId(bagAnnKh.getId());

        partialUpdatedBagAnnKh.bagName(UPDATED_BAG_NAME).description(UPDATED_DESCRIPTION);

        restBagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBagAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBagAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
        BagAnnKh testBag = bagList.get(bagList.size() - 1);
        assertThat(testBag.getBagName()).isEqualTo(UPDATED_BAG_NAME);
        assertThat(testBag.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBag.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateBagWithPatch() throws Exception {
        // Initialize the database
        bagRepository.saveAndFlush(bagAnnKh);

        int databaseSizeBeforeUpdate = bagRepository.findAll().size();

        // Update the bag using partial update
        BagAnnKh partialUpdatedBagAnnKh = new BagAnnKh();
        partialUpdatedBagAnnKh.setId(bagAnnKh.getId());

        partialUpdatedBagAnnKh.bagName(UPDATED_BAG_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        restBagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBagAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBagAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
        BagAnnKh testBag = bagList.get(bagList.size() - 1);
        assertThat(testBag.getBagName()).isEqualTo(UPDATED_BAG_NAME);
        assertThat(testBag.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBag.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingBag() throws Exception {
        int databaseSizeBeforeUpdate = bagRepository.findAll().size();
        bagAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bagAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBag() throws Exception {
        int databaseSizeBeforeUpdate = bagRepository.findAll().size();
        bagAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBag() throws Exception {
        int databaseSizeBeforeUpdate = bagRepository.findAll().size();
        bagAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBagMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bagAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bag in the database
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBag() throws Exception {
        // Initialize the database
        bagRepository.saveAndFlush(bagAnnKh);

        int databaseSizeBeforeDelete = bagRepository.findAll().size();

        // Delete the bag
        restBagMockMvc
            .perform(delete(ENTITY_API_URL_ID, bagAnnKh.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BagAnnKh> bagList = bagRepository.findAll();
        assertThat(bagList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
