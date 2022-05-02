package com.bsuir.annakhomyakova.web.rest;

import com.bsuir.annakhomyakova.domain.VersionFileAnnKh;
import com.bsuir.annakhomyakova.repository.VersionFileRepository;
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
 * REST controller for managing {@link com.bsuir.annakhomyakova.domain.VersionFileAnnKh}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VersionFileResource {

    private final Logger log = LoggerFactory.getLogger(VersionFileResource.class);

    private static final String ENTITY_NAME = "versionFile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VersionFileRepository versionFileRepository;

    public VersionFileResource(VersionFileRepository versionFileRepository) {
        this.versionFileRepository = versionFileRepository;
    }

    /**
     * {@code POST  /version-files} : Create a new versionFile.
     *
     * @param versionFileAnnKh the versionFileAnnKh to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new versionFileAnnKh, or with status {@code 400 (Bad Request)} if the versionFile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/version-files")
    public ResponseEntity<VersionFileAnnKh> createVersionFile(@Valid @RequestBody VersionFileAnnKh versionFileAnnKh)
        throws URISyntaxException {
        log.debug("REST request to save VersionFile : {}", versionFileAnnKh);
        if (versionFileAnnKh.getId() != null) {
            throw new BadRequestAlertException("A new versionFile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VersionFileAnnKh result = versionFileRepository.save(versionFileAnnKh);
        return ResponseEntity
            .created(new URI("/api/version-files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /version-files/:id} : Updates an existing versionFile.
     *
     * @param id the id of the versionFileAnnKh to save.
     * @param versionFileAnnKh the versionFileAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated versionFileAnnKh,
     * or with status {@code 400 (Bad Request)} if the versionFileAnnKh is not valid,
     * or with status {@code 500 (Internal Server Error)} if the versionFileAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/version-files/{id}")
    public ResponseEntity<VersionFileAnnKh> updateVersionFile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VersionFileAnnKh versionFileAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to update VersionFile : {}, {}", id, versionFileAnnKh);
        if (versionFileAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, versionFileAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!versionFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VersionFileAnnKh result = versionFileRepository.save(versionFileAnnKh);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, versionFileAnnKh.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /version-files/:id} : Partial updates given fields of an existing versionFile, field will ignore if it is null
     *
     * @param id the id of the versionFileAnnKh to save.
     * @param versionFileAnnKh the versionFileAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated versionFileAnnKh,
     * or with status {@code 400 (Bad Request)} if the versionFileAnnKh is not valid,
     * or with status {@code 404 (Not Found)} if the versionFileAnnKh is not found,
     * or with status {@code 500 (Internal Server Error)} if the versionFileAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/version-files/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VersionFileAnnKh> partialUpdateVersionFile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VersionFileAnnKh versionFileAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to partial update VersionFile partially : {}, {}", id, versionFileAnnKh);
        if (versionFileAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, versionFileAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!versionFileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VersionFileAnnKh> result = versionFileRepository
            .findById(versionFileAnnKh.getId())
            .map(existingVersionFile -> {
                if (versionFileAnnKh.getName() != null) {
                    existingVersionFile.setName(versionFileAnnKh.getName());
                }
                if (versionFileAnnKh.getSourceCode() != null) {
                    existingVersionFile.setSourceCode(versionFileAnnKh.getSourceCode());
                }
                if (versionFileAnnKh.getSourceCodeContentType() != null) {
                    existingVersionFile.setSourceCodeContentType(versionFileAnnKh.getSourceCodeContentType());
                }
                if (versionFileAnnKh.getCreationDate() != null) {
                    existingVersionFile.setCreationDate(versionFileAnnKh.getCreationDate());
                }

                return existingVersionFile;
            })
            .map(versionFileRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, versionFileAnnKh.getId().toString())
        );
    }

    /**
     * {@code GET  /version-files} : get all the versionFiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of versionFiles in body.
     */
    @GetMapping("/version-files")
    public List<VersionFileAnnKh> getAllVersionFiles() {
        log.debug("REST request to get all VersionFiles");
        return versionFileRepository.findAll();
    }

    /**
     * {@code GET  /version-files/:id} : get the "id" versionFile.
     *
     * @param id the id of the versionFileAnnKh to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the versionFileAnnKh, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/version-files/{id}")
    public ResponseEntity<VersionFileAnnKh> getVersionFile(@PathVariable Long id) {
        log.debug("REST request to get VersionFile : {}", id);
        Optional<VersionFileAnnKh> versionFileAnnKh = versionFileRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(versionFileAnnKh);
    }

    /**
     * {@code DELETE  /version-files/:id} : delete the "id" versionFile.
     *
     * @param id the id of the versionFileAnnKh to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/version-files/{id}")
    public ResponseEntity<Void> deleteVersionFile(@PathVariable Long id) {
        log.debug("REST request to delete VersionFile : {}", id);
        versionFileRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
