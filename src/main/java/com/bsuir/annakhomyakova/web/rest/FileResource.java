package com.bsuir.annakhomyakova.web.rest;

import com.bsuir.annakhomyakova.domain.FileAnnKh;
import com.bsuir.annakhomyakova.repository.FileRepository;
import com.bsuir.annakhomyakova.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bsuir.annakhomyakova.domain.FileAnnKh}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FileResource {

    private final Logger log = LoggerFactory.getLogger(FileResource.class);

    private static final String ENTITY_NAME = "file";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileRepository fileRepository;

    public FileResource(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * {@code POST  /files} : Create a new file.
     *
     * @param fileAnnKh the fileAnnKh to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileAnnKh, or with status {@code 400 (Bad Request)} if the file has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/files")
    public ResponseEntity<FileAnnKh> createFile(@Valid @RequestBody FileAnnKh fileAnnKh) throws URISyntaxException {
        log.debug("REST request to save File : {}", fileAnnKh);
        if (fileAnnKh.getId() != null) {
            throw new BadRequestAlertException("A new file cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FileAnnKh result = fileRepository.save(fileAnnKh);
        return ResponseEntity
            .created(new URI("/api/files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /files/:id} : Updates an existing file.
     *
     * @param id the id of the fileAnnKh to save.
     * @param fileAnnKh the fileAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileAnnKh,
     * or with status {@code 400 (Bad Request)} if the fileAnnKh is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/files/{id}")
    public ResponseEntity<FileAnnKh> updateFile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FileAnnKh fileAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to update File : {}, {}", id, fileAnnKh);
        if (fileAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FileAnnKh result = fileRepository.save(fileAnnKh);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fileAnnKh.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /files/:id} : Partial updates given fields of an existing file, field will ignore if it is null
     *
     * @param id the id of the fileAnnKh to save.
     * @param fileAnnKh the fileAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileAnnKh,
     * or with status {@code 400 (Bad Request)} if the fileAnnKh is not valid,
     * or with status {@code 404 (Not Found)} if the fileAnnKh is not found,
     * or with status {@code 500 (Internal Server Error)} if the fileAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/files/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FileAnnKh> partialUpdateFile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FileAnnKh fileAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to partial update File partially : {}, {}", id, fileAnnKh);
        if (fileAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FileAnnKh> result = fileRepository
            .findById(fileAnnKh.getId())
            .map(existingFile -> {
                if (fileAnnKh.getName() != null) {
                    existingFile.setName(fileAnnKh.getName());
                }
                if (fileAnnKh.getSourceCode() != null) {
                    existingFile.setSourceCode(fileAnnKh.getSourceCode());
                }
                if (fileAnnKh.getSourceCodeContentType() != null) {
                    existingFile.setSourceCodeContentType(fileAnnKh.getSourceCodeContentType());
                }
                if (fileAnnKh.getCreationDate() != null) {
                    existingFile.setCreationDate(fileAnnKh.getCreationDate());
                }

                return existingFile;
            })
            .map(fileRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fileAnnKh.getId().toString())
        );
    }

    /**
     * {@code GET  /files} : get all the files.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of files in body.
     */
    @GetMapping("/files")
    public List<FileAnnKh> getAllFiles(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Files");
        return fileRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /files/:id} : get the "id" file.
     *
     * @param id the id of the fileAnnKh to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileAnnKh, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/files/{id}")
    public ResponseEntity<FileAnnKh> getFile(@PathVariable Long id) {
        log.debug("REST request to get File : {}", id);
        Optional<FileAnnKh> fileAnnKh = fileRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(fileAnnKh);
    }

    /**
     * {@code DELETE  /files/:id} : delete the "id" file.
     *
     * @param id the id of the fileAnnKh to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        log.debug("REST request to delete File : {}", id);
        fileRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
