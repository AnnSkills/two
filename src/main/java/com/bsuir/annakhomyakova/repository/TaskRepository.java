package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.TaskAnnKh;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TaskAnnKh entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<TaskAnnKh, Long> {}
