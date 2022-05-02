package com.bsuir.annakhomyakova.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bsuir.annakhomyakova.IntegrationTest;
import com.bsuir.annakhomyakova.domain.FileAnnKh;
import com.bsuir.annakhomyakova.repository.FileRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link FileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_SOURCE_CODE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_SOURCE_CODE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_SOURCE_CODE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_SOURCE_CODE_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FileRepository fileRepository;

    @Mock
    private FileRepository fileRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFileMockMvc;

    private FileAnnKh fileAnnKh;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileAnnKh createEntity(EntityManager em) {
        FileAnnKh fileAnnKh = new FileAnnKh()
            .name(DEFAULT_NAME)
            .sourceCode(DEFAULT_SOURCE_CODE)
            .sourceCodeContentType(DEFAULT_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(DEFAULT_CREATION_DATE);
        return fileAnnKh;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileAnnKh createUpdatedEntity(EntityManager em) {
        FileAnnKh fileAnnKh = new FileAnnKh()
            .name(UPDATED_NAME)
            .sourceCode(UPDATED_SOURCE_CODE)
            .sourceCodeContentType(UPDATED_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);
        return fileAnnKh;
    }

    @BeforeEach
    public void initTest() {
        fileAnnKh = createEntity(em);
    }

    @Test
    @Transactional
    void createFile() throws Exception {
        int databaseSizeBeforeCreate = fileRepository.findAll().size();
        // Create the File
        restFileMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isCreated());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeCreate + 1);
        FileAnnKh testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFile.getSourceCode()).isEqualTo(DEFAULT_SOURCE_CODE);
        assertThat(testFile.getSourceCodeContentType()).isEqualTo(DEFAULT_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testFile.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void createFileWithExistingId() throws Exception {
        // Create the File with an existing ID
        fileAnnKh.setId(1L);

        int databaseSizeBeforeCreate = fileRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileRepository.findAll().size();
        // set the field null
        fileAnnKh.setName(null);

        // Create the File, which fails.

        restFileMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isBadRequest());

        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFiles() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(fileAnnKh);

        // Get all the fileList
        restFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileAnnKh.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sourceCodeContentType").value(hasItem(DEFAULT_SOURCE_CODE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].sourceCode").value(hasItem(Base64Utils.encodeToString(DEFAULT_SOURCE_CODE))))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(fileRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(fileRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(fileRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(fileRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(fileAnnKh);

        // Get the file
        restFileMockMvc
            .perform(get(ENTITY_API_URL_ID, fileAnnKh.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fileAnnKh.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sourceCodeContentType").value(DEFAULT_SOURCE_CODE_CONTENT_TYPE))
            .andExpect(jsonPath("$.sourceCode").value(Base64Utils.encodeToString(DEFAULT_SOURCE_CODE)))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFile() throws Exception {
        // Get the file
        restFileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(fileAnnKh);

        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file
        FileAnnKh updatedFileAnnKh = fileRepository.findById(fileAnnKh.getId()).get();
        // Disconnect from session so that the updates on updatedFileAnnKh are not directly saved in db
        em.detach(updatedFileAnnKh);
        updatedFileAnnKh
            .name(UPDATED_NAME)
            .sourceCode(UPDATED_SOURCE_CODE)
            .sourceCodeContentType(UPDATED_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);

        restFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFileAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFileAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
        FileAnnKh testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFile.getSourceCode()).isEqualTo(UPDATED_SOURCE_CODE);
        assertThat(testFile.getSourceCodeContentType()).isEqualTo(UPDATED_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testFile.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void putNonExistingFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        fileAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fileAnnKh.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        fileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        fileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFileWithPatch() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(fileAnnKh);

        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file using partial update
        FileAnnKh partialUpdatedFileAnnKh = new FileAnnKh();
        partialUpdatedFileAnnKh.setId(fileAnnKh.getId());

        partialUpdatedFileAnnKh.name(UPDATED_NAME);

        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFileAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
        FileAnnKh testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFile.getSourceCode()).isEqualTo(DEFAULT_SOURCE_CODE);
        assertThat(testFile.getSourceCodeContentType()).isEqualTo(DEFAULT_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testFile.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateFileWithPatch() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(fileAnnKh);

        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file using partial update
        FileAnnKh partialUpdatedFileAnnKh = new FileAnnKh();
        partialUpdatedFileAnnKh.setId(fileAnnKh.getId());

        partialUpdatedFileAnnKh
            .name(UPDATED_NAME)
            .sourceCode(UPDATED_SOURCE_CODE)
            .sourceCodeContentType(UPDATED_SOURCE_CODE_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);

        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFileAnnKh))
            )
            .andExpect(status().isOk());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
        FileAnnKh testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFile.getSourceCode()).isEqualTo(UPDATED_SOURCE_CODE);
        assertThat(testFile.getSourceCodeContentType()).isEqualTo(UPDATED_SOURCE_CODE_CONTENT_TYPE);
        assertThat(testFile.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        fileAnnKh.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fileAnnKh.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        fileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        fileAnnKh.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fileAnnKh))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the File in the database
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(fileAnnKh);

        int databaseSizeBeforeDelete = fileRepository.findAll().size();

        // Delete the file
        restFileMockMvc
            .perform(delete(ENTITY_API_URL_ID, fileAnnKh.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FileAnnKh> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
