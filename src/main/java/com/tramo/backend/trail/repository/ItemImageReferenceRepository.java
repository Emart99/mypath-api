package com.tramo.backend.trail.repository;

import com.tramo.backend.trail.entity.ItemImageReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageReferenceRepository extends JpaRepository<ItemImageReference, Long> {
    void deleteByItemId(Long itemId);

    @Query("SELECT COUNT(r) > 0 FROM ItemImageReference r " +
            "WHERE r.url = :url AND r.item.id <> :excludeItemId AND r.item.project.owner.id = :ownerId")
    boolean existsOtherItemReferencingUrl(@Param("ownerId") Long ownerId, @Param("url") String url,
                                           @Param("excludeItemId") Long excludeItemId);
}
