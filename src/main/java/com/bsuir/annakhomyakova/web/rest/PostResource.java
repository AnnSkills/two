package com.bsuir.annakhomyakova.web.rest;

import com.bsuir.annakhomyakova.domain.PostAnnKh;
import com.bsuir.annakhomyakova.repository.PostRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bsuir.annakhomyakova.domain.PostAnnKh}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PostResource {

    private final Logger log = LoggerFactory.getLogger(PostResource.class);

    private static final String ENTITY_NAME = "post";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostRepository postRepository;

    public PostResource(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * {@code POST  /posts} : Create a new post.
     *
     * @param postAnnKh the postAnnKh to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postAnnKh, or with status {@code 400 (Bad Request)} if the post has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/posts")
    public ResponseEntity<PostAnnKh> createPost(@Valid @RequestBody PostAnnKh postAnnKh) throws URISyntaxException {
        log.debug("REST request to save Post : {}", postAnnKh);
        if (postAnnKh.getId() != null) {
            throw new BadRequestAlertException("A new post cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PostAnnKh result = postRepository.save(postAnnKh);
        return ResponseEntity
            .created(new URI("/api/posts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /posts/:id} : Updates an existing post.
     *
     * @param id the id of the postAnnKh to save.
     * @param postAnnKh the postAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postAnnKh,
     * or with status {@code 400 (Bad Request)} if the postAnnKh is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostAnnKh> updatePost(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PostAnnKh postAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to update Post : {}, {}", id, postAnnKh);
        if (postAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PostAnnKh result = postRepository.save(postAnnKh);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postAnnKh.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /posts/:id} : Partial updates given fields of an existing post, field will ignore if it is null
     *
     * @param id the id of the postAnnKh to save.
     * @param postAnnKh the postAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postAnnKh,
     * or with status {@code 400 (Bad Request)} if the postAnnKh is not valid,
     * or with status {@code 404 (Not Found)} if the postAnnKh is not found,
     * or with status {@code 500 (Internal Server Error)} if the postAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/posts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostAnnKh> partialUpdatePost(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PostAnnKh postAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to partial update Post partially : {}, {}", id, postAnnKh);
        if (postAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostAnnKh> result = postRepository
            .findById(postAnnKh.getId())
            .map(existingPost -> {
                if (postAnnKh.getTitle() != null) {
                    existingPost.setTitle(postAnnKh.getTitle());
                }
                if (postAnnKh.getContent() != null) {
                    existingPost.setContent(postAnnKh.getContent());
                }
                if (postAnnKh.getPicture() != null) {
                    existingPost.setPicture(postAnnKh.getPicture());
                }
                if (postAnnKh.getPictureContentType() != null) {
                    existingPost.setPictureContentType(postAnnKh.getPictureContentType());
                }
                if (postAnnKh.getDate() != null) {
                    existingPost.setDate(postAnnKh.getDate());
                }

                return existingPost;
            })
            .map(postRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postAnnKh.getId().toString())
        );
    }

    /**
     * {@code GET  /posts} : get all the posts.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of posts in body.
     */
    @GetMapping("/posts")
    public ResponseEntity<List<PostAnnKh>> getAllPosts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Posts");
        Page<PostAnnKh> page;
        if (eagerload) {
            page = postRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = postRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /posts/:id} : get the "id" post.
     *
     * @param id the id of the postAnnKh to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postAnnKh, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostAnnKh> getPost(@PathVariable Long id) {
        log.debug("REST request to get Post : {}", id);
        Optional<PostAnnKh> postAnnKh = postRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(postAnnKh);
    }

    /**
     * {@code DELETE  /posts/:id} : delete the "id" post.
     *
     * @param id the id of the postAnnKh to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        log.debug("REST request to delete Post : {}", id);
        postRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
