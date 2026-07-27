package com.tramo.backend.trail.repository;

import com.tramo.backend.trail.entity.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrailRepository extends JpaRepository<Trail, Long> {
    // Creation order — without an ORDER BY the DB returns rows in arbitrary
    // (reload-dependent) order and the sidebar shuffles. Join-fetch project since
    // callers read trail.getProject() (now LAZY) while mapping to DTOs.
    @Query("select t from Trail t join fetch t.project where t.project.id = :projectId order by t.id asc")
    List<Trail> findByProjectId(@Param("projectId") Long projectId);

    // Ownership checks read trail.getProject().getOwner() outside any transaction,
    // so project must come back already initialized.
    @Query("select t from Trail t join fetch t.project where t.id = :id")
    Optional<Trail> findByIdWithProject(@Param("id") Long id);

    // id + title only: association-target title lookups don't need each trail's
    // EAGER project/forkedFrom associations pulled in.
    @Query("SELECT t.id, t.title FROM Trail t WHERE t.id IN :ids")
    List<Object[]> findIdTitleByIdIn(@Param("ids") Collection<Long> ids);
}
