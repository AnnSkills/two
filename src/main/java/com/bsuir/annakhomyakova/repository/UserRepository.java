package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.UserAnnKh;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link UserAnnKh} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserAnnKh, Long> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";
    Optional<UserAnnKh> findOneByActivationKey(String activationKey);
    List<UserAnnKh> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);
    Optional<UserAnnKh> findOneByResetKey(String resetKey);
    Optional<UserAnnKh> findOneByEmailIgnoreCase(String email);
    Optional<UserAnnKh> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<UserAnnKh> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<UserAnnKh> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<UserAnnKh> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
