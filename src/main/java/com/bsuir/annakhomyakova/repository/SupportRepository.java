package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.SupportAnnKh;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SupportAnnKh entity.
 */
@Repository
public interface SupportRepository extends JpaRepository<SupportAnnKh, Long> {
    @Query("select support from SupportAnnKh support where support.user.login = ?#{principal.username}")
    List<SupportAnnKh> findByUserIsCurrentUser();

    default Optional<SupportAnnKh> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SupportAnnKh> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SupportAnnKh> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct support from SupportAnnKh support left join fetch support.user",
        countQuery = "select count(distinct support) from SupportAnnKh support"
    )
    Page<SupportAnnKh> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct support from SupportAnnKh support left join fetch support.user")
    List<SupportAnnKh> findAllWithToOneRelationships();

    @Query("select support from SupportAnnKh support left join fetch support.user where support.id =:id")
    Optional<SupportAnnKh> findOneWithToOneRelationships(@Param("id") Long id);
}
