package com.bsuir.annakhomyakova.repository;

import com.bsuir.annakhomyakova.domain.FileAnnKh;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FileAnnKh entity.
 */
@Repository
public interface FileRepository extends JpaRepository<FileAnnKh, Long> {
    @Query("select file from FileAnnKh file where file.user.login = ?#{principal.username}")
    List<FileAnnKh> findByUserIsCurrentUser();

    default Optional<FileAnnKh> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FileAnnKh> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FileAnnKh> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct file from FileAnnKh file left join fetch file.user",
        countQuery = "select count(distinct file) from FileAnnKh file"
    )
    Page<FileAnnKh> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct file from FileAnnKh file left join fetch file.user")
    List<FileAnnKh> findAllWithToOneRelationships();

    @Query("select file from FileAnnKh file left join fetch file.user where file.id =:id")
    Optional<FileAnnKh> findOneWithToOneRelationships(@Param("id") Long id);
}
