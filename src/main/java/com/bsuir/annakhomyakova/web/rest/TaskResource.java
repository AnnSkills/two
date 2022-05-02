package com.bsuir.annakhomyakova.web.rest;

import com.bsuir.annakhomyakova.domain.TaskAnnKh;
import com.bsuir.annakhomyakova.repository.TaskRepository;
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
 * REST controller for managing {@link com.bsuir.annakhomyakova.domain.TaskAnnKh}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    private static final String ENTITY_NAME = "task";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskRepository taskRepository;

    public TaskResource(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * {@code POST  /tasks} : Create a new task.
     *
     * @param taskAnnKh the taskAnnKh to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskAnnKh, or with status {@code 400 (Bad Request)} if the task has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tasks")
    public ResponseEntity<TaskAnnKh> createTask(@Valid @RequestBody TaskAnnKh taskAnnKh) throws URISyntaxException {
        log.debug("REST request to save Task : {}", taskAnnKh);
        if (taskAnnKh.getId() != null) {
            throw new BadRequestAlertException("A new task cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskAnnKh result = taskRepository.save(taskAnnKh);
        return ResponseEntity
            .created(new URI("/api/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tasks/:id} : Updates an existing task.
     *
     * @param id the id of the taskAnnKh to save.
     * @param taskAnnKh the taskAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskAnnKh,
     * or with status {@code 400 (Bad Request)} if the taskAnnKh is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskAnnKh> updateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaskAnnKh taskAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to update Task : {}, {}", id, taskAnnKh);
        if (taskAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaskAnnKh result = taskRepository.save(taskAnnKh);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskAnnKh.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tasks/:id} : Partial updates given fields of an existing task, field will ignore if it is null
     *
     * @param id the id of the taskAnnKh to save.
     * @param taskAnnKh the taskAnnKh to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskAnnKh,
     * or with status {@code 400 (Bad Request)} if the taskAnnKh is not valid,
     * or with status {@code 404 (Not Found)} if the taskAnnKh is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskAnnKh couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tasks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskAnnKh> partialUpdateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskAnnKh taskAnnKh
    ) throws URISyntaxException {
        log.debug("REST request to partial update Task partially : {}, {}", id, taskAnnKh);
        if (taskAnnKh.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAnnKh.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskAnnKh> result = taskRepository
            .findById(taskAnnKh.getId())
            .map(existingTask -> {
                if (taskAnnKh.getTaskName() != null) {
                    existingTask.setTaskName(taskAnnKh.getTaskName());
                }
                if (taskAnnKh.getDescription() != null) {
                    existingTask.setDescription(taskAnnKh.getDescription());
                }
                if (taskAnnKh.getDeadline() != null) {
                    existingTask.setDeadline(taskAnnKh.getDeadline());
                }

                return existingTask;
            })
            .map(taskRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskAnnKh.getId().toString())
        );
    }

    /**
     * {@code GET  /tasks} : get all the tasks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks in body.
     */
    @GetMapping("/tasks")
    public List<TaskAnnKh> getAllTasks() {
        log.debug("REST request to get all Tasks");
        return taskRepository.findAll();
    }

    /**
     * {@code GET  /tasks/:id} : get the "id" task.
     *
     * @param id the id of the taskAnnKh to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskAnnKh, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskAnnKh> getTask(@PathVariable Long id) {
        log.debug("REST request to get Task : {}", id);
        Optional<TaskAnnKh> taskAnnKh = taskRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(taskAnnKh);
    }

    /**
     * {@code DELETE  /tasks/:id} : delete the "id" task.
     *
     * @param id the id of the taskAnnKh to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.debug("REST request to delete Task : {}", id);
        taskRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
