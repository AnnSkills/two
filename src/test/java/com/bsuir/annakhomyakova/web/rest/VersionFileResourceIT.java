package com.bsuir.annakhomyakova.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bsuir.annakhomyakova.IntegrationTest;
import com.bsuir.annakhomyakova.domain.VersionFileAnnKh;
import com.bsuir.annakhomyakova.repository.VersionFileRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link VersionFileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VersionFileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_SOURCE_CODE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_SOURCE_CODE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_SOURCE_CODE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_SOURCE_CODE_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/version-files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VersionFileRepository versionFileRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVersionFileMockMvc;

    private VersionFileAnnKh versionFileAnnKh;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VersionFileAnnKh createEntity(EntityManager em) {
        VersionFileAnnKh versionFileAnnKh = new VersionFileAnnKh()
            .name(DEFAULT_NAME)
            .sourceCode(DEFAULT_SOURCE_CODE)
            .sourceCodeContentType(DEFAULT_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(DEFAULT_CREATION_DATE);
        return versionFileAnnKh;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VersionFileAnnKh createUpdatedEntity(EntityManager em) {
        VersionFileAnnKh versionFileAnnKh = new VersionFileAnnKh()
            .name(UPDATED_NAME)
            .sourceCode(UPDATED_SOURCE_CODE)
            .sourceCodeContentType(UPDATED_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);
        return versionFileAnnKh;
    }

    @BeforeEach
    public void initTest() {
        versionFileAnnKh = createEntity(em);
    }

    @Test
    @Transactional
    void createVersionFile() throws Exception {
        int databaseSizeBeforeCreate = versionFileRepository.findAll().size();
        // Create the VersionFile
        restVersionFileMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isCreated());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeCreate + 1);
        VersionFileAnnKh testVersionFile = versionFileList.get(versionFileList.size() - 1);
        assertThat(testVersionFile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testVersionFile.getSourceCode()).isEqualTo(DEFAULT_SOURCE_CODE);
        assertThat(testVersionFile.getSourceCodeContentType()).isEqualTo(DEFAULT_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testVersionFile.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void createVersionFileWithExistingId() throws Exception {
        // Create the VersionFile with an existing ID
        versionFileAnnKh.setId(1L);

        int databaseSizeBeforeCreate = versionFileRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVersionFileMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = versionFileRepository.findAll().size();
        // set the field null
        versionFileAnnKh.setName(null);

        // Create the VersionFile, which fails.

        restVersionFileMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isBadRequest());

        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVersionFiles() throws Exception {
        // Initialize the database
        versionFileRepository.saveAndFlush(versionFileAnnKh);

        // Get all the versionFileList
        restVersionFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(versionFileAnnKh.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sourceCodeContentType").value(hasItem(DEFAULT_SOURCE_CODE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].sourceCode").value(hasItem(Base64Utils.encodeToString(DEFAULT_SOURCE_CODE))))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    void getVersionFile() throws Exception {
        // Initialize the database
        versionFileRepository.saveAndFlush(versionFileAnnKh);

        // Get the versionFile
        restVersionFileMockMvc
            .perform(get(ENTITY_API_URL_ID, versionFileAnnKh.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(versionFileAnnKh.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sourceCodeContentType").value(DEFAULT_SOURCE_CODE_CONTENT_TYPE))
            .andExpect(jsonPath("$.sourceCode").value(Base64Utils.encodeToString(DEFAULT_SOURCE_CODE)))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingVersionFile() throws Exception {
        // Get the versionFile
        restVersionFileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVersionFile() throws Exception {
        // Initialize the database
        versionFileRepository.saveAndFlush(versionFileAnnKh);

        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();

        // Update the versionFile
        VersionFileAnnKh updatedVersionFileAnnKh = versionFileRepository.findById(versionFileAnnKh.getId()).get();
        // Disconnect from session so that the updates on updatedVersionFileAnnKh are not directly saved in db
        em.detach(updatedVersionFileAnnKh);
        updatedVersionFileAnnKh
            .name(UPDATED_NAME)
            .sourceCode(UPDATED_SOURCE_CODE)
            .sourceCodeContentType(UPDATED_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);

        restVersionFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVersionFileAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVersionFileAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
        VersionFileAnnKh testVersionFile = versionFileList.get(versionFileList.size() - 1);
        assertThat(testVersionFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVersionFile.getSourceCode()).isEqualTo(UPDATED_SOURCE_CODE);
        assertThat(testVersionFile.getSourceCodeContentType()).isEqualTo(UPDATED_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testVersionFile.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void putNonExistingVersionFile() throws Exception {
        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();
        versionFileAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVersionFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, versionFileAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVersionFile() throws Exception {
        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();
        versionFileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVersionFile() throws Exception {
        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();
        versionFileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionFileMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVersionFileWithPatch() throws Exception {
        // Initialize the database
        versionFileRepository.saveAndFlush(versionFileAnnKh);

        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();

        // Update the versionFile using partial update
        VersionFileAnnKh partialUpdatedVersionFileAnnKh = new VersionFileAnnKh();
        partialUpdatedVersionFileAnnKh.setId(versionFileAnnKh.getId());

        restVersionFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVersionFileAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVersionFileAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
        VersionFileAnnKh testVersionFile = versionFileList.get(versionFileList.size() - 1);
        assertThat(testVersionFile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testVersionFile.getSourceCode()).isEqualTo(DEFAULT_SOURCE_CODE);
        assertThat(testVersionFile.getSourceCodeContentType()).isEqualTo(DEFAULT_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testVersionFile.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateVersionFileWithPatch() throws Exception {
        // Initialize the database
        versionFileRepository.saveAndFlush(versionFileAnnKh);

        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();

        // Update the versionFile using partial update
        VersionFileAnnKh partialUpdatedVersionFileAnnKh = new VersionFileAnnKh();
        partialUpdatedVersionFileAnnKh.setId(versionFileAnnKh.getId());

        partialUpdatedVersionFileAnnKh
            .name(UPDATED_NAME)
            .sourceCode(UPDATED_SOURCE_CODE)
            .sourceCodeContentType(UPDATED_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);

        restVersionFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVersionFileAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVersionFileAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
        VersionFileAnnKh testVersionFile = versionFileList.get(versionFileList.size() - 1);
        assertThat(testVersionFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testVersionFile.getSourceCode()).isEqualTo(UPDATED_SOURCE_CODE);
        assertThat(testVersionFile.getSourceCodeContentType()).isEqualTo(UPDATED_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testVersionFile.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingVersionFile() throws Exception {
        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();
        versionFileAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVersionFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, versionFileAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVersionFile() throws Exception {
        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();
        versionFileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVersionFile() throws Exception {
        int databaseSizeBeforeUpdate = versionFileRepository.findAll().size();
        versionFileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionFileMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(versionFileAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VersionFile in the database
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVersionFile() throws Exception {
        // Initialize the database
        versionFileRepository.saveAndFlush(versionFileAnnKh);

        int databaseSizeBeforeDelete = versionFileRepository.findAll().size();

        // Delete the versionFile
        restVersionFileMockMvc
            .perform(delete(ENTITY_API_URL_ID, versionFileAnnKh.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<VersionFileAnnKh> versionFileList = versionFileRepository.findAll();
        assertThat(versionFileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
