package com.tramo.backend.trail.repository;

import com.tramo.backend.trail.entity.Association;
import com.tramo.backend.trail.entity.AssociationTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssociationRepository extends JpaRepository<Association, Long> {
    List<Association> findBySourceItemId(Long sourceItemId);

    // Batched lookup for building the public project's associations without an
    // N+1 (one findBySourceItemId per item).
    List<Association> findBySourceItemIdIn(Collection<Long> sourceItemIds);

    List<Association> findByTargetTypeAndTargetId(AssociationTargetType targetType, Long targetId);

    Optional<Association> findBySourceItemIdAndTargetTypeAndTargetId(
            Long sourceItemId, AssociationTargetType targetType, Long targetId);

    void deleteBySourceItemId(Long sourceItemId);

    void deleteByTargetTypeAndTargetId(AssociationTargetType targetType, Long targetId);
}
