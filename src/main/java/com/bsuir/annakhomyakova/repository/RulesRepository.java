package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.RulesAnnKh;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RulesAnnKh entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RulesRepository extends JpaRepository<RulesAnnKh, Long> {}
