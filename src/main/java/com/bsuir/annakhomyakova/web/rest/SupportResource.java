package com.bsuir.annakhomyakova.web.rest;

import com.bsuir.annakhomyakova.domain.SupportAnnKh;
import com.bsuir.annakhomyakova.repository.SupportRepository;
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
 * REST controller for managing {@link com.bsuir.annakhomyakova.domain.SupportAnnKh}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SupportResource {

    private final Logger log = LoggerFactory.getLogger(SupportResource.class);

    private static final String ENTITY_NAME = "support";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupportRepository supportRepository;

    public SupportResource(SupportRepository supportRepository) {
        this.supportRepository = supportRepository;
    }

    /**
     * {@code POST  /supports} : Create a new support.
     *
     * @param supportAnnKh the supportAnnKh to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supportAnnKh, or with status {@code 400 (Bad Request)} if the support has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/supports")
    public ResponseEntity<SupportAnnKh> createSupport(@Valid @RequestBody SupportAnnKh supportAnnKh) throws URISyntaxException {
        log.debug("REST request to save Support : {}", supportAnnKh);
        if (supportAnnKh.getId() != null) {
            throw new BadRequestAlertException("A new support cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SupportAnnKh result = supportRepository.save(supportAnnKh);
        return ResponseEntity
            .created(new URI("/api/supports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /supports/:id} : Updates an existing support.
     *
     * @param id the id of the supportAnnKh to save.
     * @param supportAnnKh the supportAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supportAnnKh,
     * or with status {@code 400 (Bad Request)} if the supportAnnKh is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supportAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/supports/{id}")
    public ResponseEntity<SupportAnnKh> updateSupport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupportAnnKh supportAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to update Support : {}, {}", id, supportAnnKh);
        if (supportAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supportAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SupportAnnKh result = supportRepository.save(supportAnnKh);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supportAnnKh.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /supports/:id} : Partial updates given fields of an existing support, field will ignore if it is null
     *
     * @param id the id of the supportAnnKh to save.
     * @param supportAnnKh the supportAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supportAnnKh,
     * or with status {@code 400 (Bad Request)} if the supportAnnKh is not valid,
     * or with status {@code 404 (Not Found)} if the supportAnnKh is not found,
     * or with status {@code 500 (Internal Server Error)} if the supportAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/supports/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SupportAnnKh> partialUpdateSupport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupportAnnKh supportAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to partial update Support partially : {}, {}", id, supportAnnKh);
        if (supportAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supportAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupportAnnKh> result = supportRepository
            .findById(supportAnnKh.getId())
            .map(existingSupport -> {
                if (supportAnnKh.getTopic() != null) {
                    existingSupport.setTopic(supportAnnKh.getTopic());
                }
                if (supportAnnKh.getEmail() != null) {
                    existingSupport.setEmail(supportAnnKh.getEmail());
                }
                if (supportAnnKh.getPhone() != null) {
                    existingSupport.setPhone(supportAnnKh.getPhone());
                }
                if (supportAnnKh.getDescription() != null) {
                    existingSupport.setDescription(supportAnnKh.getDescription());
                }

                return existingSupport;
            })
            .map(supportRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supportAnnKh.getId().toString())
        );
    }

    /**
     * {@code GET  /supports} : get all the supports.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of supports in body.
     */
    @GetMapping("/supports")
    public List<SupportAnnKh> getAllSupports(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Supports");
        return supportRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /supports/:id} : get the "id" support.
     *
     * @param id the id of the supportAnnKh to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supportAnnKh, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/supports/{id}")
    public ResponseEntity<SupportAnnKh> getSupport(@PathVariable Long id) {
        log.debug("REST request to get Support : {}", id);
        Optional<SupportAnnKh> supportAnnKh = supportRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(supportAnnKh);
    }

    /**
     * {@code DELETE  /supports/:id} : delete the "id" support.
     *
     * @param id the id of the supportAnnKh to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/supports/{id}")
    public ResponseEntity<Void> deleteSupport(@PathVariable Long id) {
        log.debug("REST request to delete Support : {}", id);
        supportRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
