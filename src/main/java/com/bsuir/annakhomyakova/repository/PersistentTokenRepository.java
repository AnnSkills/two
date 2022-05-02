package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.PersistentToken;
import com.bsuir.annakhomyakova.domain.UserAnnKh;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the {@link PersistentToken} entity.
 */
public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {
    List<PersistentToken> findByUser(UserAnnKh user);

    List<PersistentToken> findByTokenDateBefore(LocalDate localDate);
}
