package com.bsuir.annakhomyakova.web.rest;

import com.bsuir.annakhomyakova.domain.RulesAnnKh;
import com.bsuir.annakhomyakova.repository.RulesRepository;
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
 * REST controller for managing {@link com.bsuir.annakhomyakova.domain.RulesAnnKh}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RulesResource {

    private final Logger log = LoggerFactory.getLogger(RulesResource.class);

    private static final String ENTITY_NAME = "rules";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RulesRepository rulesRepository;

    public RulesResource(RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    /**
     * {@code POST  /rules} : Create a new rules.
     *
     * @param rulesAnnKh the rulesAnnKh to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rulesAnnKh, or with status {@code 400 (Bad Request)} if the rules has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rules")
    public ResponseEntity<RulesAnnKh> createRules(@Valid @RequestBody RulesAnnKh rulesAnnKh) throws URISyntaxException {
        log.debug("REST request to save Rules : {}", rulesAnnKh);
        if (rulesAnnKh.getId() != null) {
            throw new BadRequestAlertException("A new rules cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RulesAnnKh result = rulesRepository.save(rulesAnnKh);
        return ResponseEntity
            .created(new URI("/api/rules/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rules/:id} : Updates an existing rules.
     *
     * @param id the id of the rulesAnnKh to save.
     * @param rulesAnnKh the rulesAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rulesAnnKh,
     * or with status {@code 400 (Bad Request)} if the rulesAnnKh is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rulesAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rules/{id}")
    public ResponseEntity<RulesAnnKh> updateRules(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RulesAnnKh rulesAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to update Rules : {}, {}", id, rulesAnnKh);
        if (rulesAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rulesAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rulesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RulesAnnKh result = rulesRepository.save(rulesAnnKh);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rulesAnnKh.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /rules/:id} : Partial updates given fields of an existing rules, field will ignore if it is null
     *
     * @param id the id of the rulesAnnKh to save.
     * @param rulesAnnKh the rulesAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rulesAnnKh,
     * or with status {@code 400 (Bad Request)} if the rulesAnnKh is not valid,
     * or with status {@code 404 (Not Found)} if the rulesAnnKh is not found,
     * or with status {@code 500 (Internal Server Error)} if the rulesAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rules/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RulesAnnKh> partialUpdateRules(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RulesAnnKh rulesAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to partial update Rules partially : {}, {}", id, rulesAnnKh);
        if (rulesAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rulesAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rulesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RulesAnnKh> result = rulesRepository
            .findById(rulesAnnKh.getId())
            .map(existingRules -> {
                if (rulesAnnKh.getName() != null) {
                    existingRules.setName(rulesAnnKh.getName());
                }
                if (rulesAnnKh.getCode() != null) {
                    existingRules.setCode(rulesAnnKh.getCode());
                }
                if (rulesAnnKh.getRequirements() != null) {
                    existingRules.setRequirements(rulesAnnKh.getRequirements());
                }
                if (rulesAnnKh.getRequirementsContentType() != null) {
                    existingRules.setRequirementsContentType(rulesAnnKh.getRequirementsContentType());
                }

                return existingRules;
            })
            .map(rulesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rulesAnnKh.getId().toString())
        );
    }

    /**
     * {@code GET  /rules} : get all the rules.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rules in body.
     */
    @GetMapping("/rules")
    public List<RulesAnnKh> getAllRules() {
        log.debug("REST request to get all Rules");
        return rulesRepository.findAll();
    }

    /**
     * {@code GET  /rules/:id} : get the "id" rules.
     *
     * @param id the id of the rulesAnnKh to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rulesAnnKh, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rules/{id}")
    public ResponseEntity<RulesAnnKh> getRules(@PathVariable Long id) {
        log.debug("REST request to get Rules : {}", id);
        Optional<RulesAnnKh> rulesAnnKh = rulesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(rulesAnnKh);
    }

    /**
     * {@code DELETE  /rules/:id} : delete the "id" rules.
     *
     * @param id the id of the rulesAnnKh to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Void> deleteRules(@PathVariable Long id) {
        log.debug("REST request to delete Rules : {}", id);
        rulesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
