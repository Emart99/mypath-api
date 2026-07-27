package com.tramo.backend.trail.repository;

import com.tramo.backend.trail.entity.TrailItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrailItemRepository extends JpaRepository<TrailItem, Long> {
    // Secondary id sort: attach sets orderIndex = count, so after a detach two
    // rows can share an index — without a tiebreak their order flips between
    // reloads. id asc = attach order, matching the frontend's append.
    // Join-fetch item, its EAGER content, and the step's association so rendering a
    // trail's steps is one query instead of N+1 (item + content + association per step).
    @Query("SELECT pi FROM TrailItem pi JOIN FETCH pi.item i LEFT JOIN FETCH i.content " +
            "LEFT JOIN FETCH pi.association WHERE pi.trail.id = :trailId ORDER BY pi.orderIndex ASC, pi.id ASC")
    List<TrailItem> findByTrailIdOrderByOrderIndexAsc(@Param("trailId") Long trailId);

    // Join-fetch trail and its project: callers resolve item ownership and the
    // owning project via pi.getTrail().getProject(), both now LAZY.
    @Query("SELECT pi FROM TrailItem pi JOIN FETCH pi.trail t LEFT JOIN FETCH t.project WHERE pi.item.id = :itemId")
    List<TrailItem> findByItemId(@Param("itemId") Long itemId);

    int countByTrailId(Long trailId);

    @Query("SELECT pi FROM TrailItem pi JOIN FETCH pi.item i LEFT JOIN FETCH i.content WHERE pi.trail.id IN :trailIds ORDER BY pi.orderIndex ASC, pi.id ASC")
    List<TrailItem> findByTrailIdInWithItemAndContent(@Param("trailIds") List<Long> trailIds);
}
