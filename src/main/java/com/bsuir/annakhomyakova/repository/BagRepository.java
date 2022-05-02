package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.BagAnnKh;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BagAnnKh entity.
 */
@Repository
public interface BagRepository extends JpaRepository<BagAnnKh, Long> {
    @Query("select bag from BagAnnKh bag where bag.user.login = ?#{principal.username}")
    List<BagAnnKh> findByUserIsCurrentUser();

    default Optional<BagAnnKh> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BagAnnKh> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BagAnnKh> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct bag from BagAnnKh bag left join fetch bag.user",
        countQuery = "select count(distinct bag) from BagAnnKh bag"
    )
    Page<BagAnnKh> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct bag from BagAnnKh bag left join fetch bag.user")
    List<BagAnnKh> findAllWithToOneRelationships();

    @Query("select bag from BagAnnKh bag left join fetch bag.user where bag.id =:id")
    Optional<BagAnnKh> findOneWithToOneRelationships(@Param("id") Long id);
}
