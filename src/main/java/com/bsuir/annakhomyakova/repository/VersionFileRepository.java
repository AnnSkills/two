package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.VersionFileAnnKh;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the VersionFileAnnKh entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersionFileRepository extends JpaRepository<VersionFileAnnKh, Long> {}
