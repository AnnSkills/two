package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.PostAnnKh;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PostAnnKh entity.
 */
@Repository
public interface PostRepository extends JpaRepository<PostAnnKh, Long> {
    @Query("select post from PostAnnKh post where post.user.login = ?#{principal.username}")
    List<PostAnnKh> findByUserIsCurrentUser();

    default Optional<PostAnnKh> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PostAnnKh> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PostAnnKh> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct post from PostAnnKh post left join fetch post.user",
        countQuery = "select count(distinct post) from PostAnnKh post"
    )
    Page<PostAnnKh> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct post from PostAnnKh post left join fetch post.user")
    List<PostAnnKh> findAllWithToOneRelationships();

    @Query("select post from PostAnnKh post left join fetch post.user where post.id =:id")
    Optional<PostAnnKh> findOneWithToOneRelationships(@Param("id") Long id);
}
