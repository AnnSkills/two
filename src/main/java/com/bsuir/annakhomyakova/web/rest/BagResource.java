package com.bsuir.annakhomyakova.web.rest;

import com.bsuir.annakhomyakova.domain.BagAnnKh;
import com.bsuir.annakhomyakova.repository.BagRepository;
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
 * REST controller for managing {@link com.bsuir.annakhomyakova.domain.BagAnnKh}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BagResource {

    private final Logger log = LoggerFactory.getLogger(BagResource.class);

    private static final String ENTITY_NAME = "bag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BagRepository bagRepository;

    public BagResource(BagRepository bagRepository) {
        this.bagRepository = bagRepository;
    }

    /**
     * {@code POST  /bags} : Create a new bag.
     *
     * @param bagAnnKh the bagAnnKh to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bagAnnKh, or with status {@code 400 (Bad Request)} if the bag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bags")
    public ResponseEntity<BagAnnKh> createBag(@Valid @RequestBody BagAnnKh bagAnnKh) throws URISyntaxException {
        log.debug("REST request to save Bag : {}", bagAnnKh);
        if (bagAnnKh.getId() != null) {
            throw new BadRequestAlertException("A new bag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BagAnnKh result = bagRepository.save(bagAnnKh);
        return ResponseEntity
            .created(new URI("/api/bags/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bags/:id} : Updates an existing bag.
     *
     * @param id the id of the bagAnnKh to save.
     * @param bagAnnKh the bagAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bagAnnKh,
     * or with status {@code 400 (Bad Request)} if the bagAnnKh is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bagAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bags/{id}")
    public ResponseEntity<BagAnnKh> updateBag(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BagAnnKh bagAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to update Bag : {}, {}", id, bagAnnKh);
        if (bagAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bagAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BagAnnKh result = bagRepository.save(bagAnnKh);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bagAnnKh.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bags/:id} : Partial updates given fields of an existing bag, field will ignore if it is null
     *
     * @param id the id of the bagAnnKh to save.
     * @param bagAnnKh the bagAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bagAnnKh,
     * or with status {@code 400 (Bad Request)} if the bagAnnKh is not valid,
     * or with status {@code 404 (Not Found)} if the bagAnnKh is not found,
     * or with status {@code 500 (Internal Server Error)} if the bagAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bags/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BagAnnKh> partialUpdateBag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BagAnnKh bagAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to partial update Bag partially : {}, {}", id, bagAnnKh);
        if (bagAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bagAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BagAnnKh> result = bagRepository
            .findById(bagAnnKh.getId())
            .map(existingBag -> {
                if (bagAnnKh.getBagName() != null) {
                    existingBag.setBagName(bagAnnKh.getBagName());
                }
                if (bagAnnKh.getDescription() != null) {
                    existingBag.setDescription(bagAnnKh.getDescription());
                }
                if (bagAnnKh.getStatus() != null) {
                    existingBag.setStatus(bagAnnKh.getStatus());
                }

                return existingBag;
            })
            .map(bagRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bagAnnKh.getId().toString())
        );
    }

    /**
     * {@code GET  /bags} : get all the bags.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bags in body.
     */
    @GetMapping("/bags")
    public List<BagAnnKh> getAllBags(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Bags");
        return bagRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /bags/:id} : get the "id" bag.
     *
     * @param id the id of the bagAnnKh to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bagAnnKh, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bags/{id}")
    public ResponseEntity<BagAnnKh> getBag(@PathVariable Long id) {
        log.debug("REST request to get Bag : {}", id);
        Optional<BagAnnKh> bagAnnKh = bagRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(bagAnnKh);
    }

    /**
     * {@code DELETE  /bags/:id} : delete the "id" bag.
     *
     * @param id the id of the bagAnnKh to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bags/{id}")
    public ResponseEntity<Void> deleteBag(@PathVariable Long id) {
        log.debug("REST request to delete Bag : {}", id);
        bagRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
