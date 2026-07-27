package com.tramo.backend.trail.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_trail_item_trail", columnList = "trail_id"),
        @Index(name = "idx_trail_item_item", columnList = "item_id"),
        @Index(name = "idx_trail_item_association", columnList = "association_id"),
})
public class TrailItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trail trail;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    int orderIndex;

    // Human text linking this step to the next — what makes a trail studyable on its own.
    @Column(columnDefinition = "TEXT")
    private String annotation;

    // Which graph association was used to jump here from the previous step; null = deliberate jump.
    @ManyToOne(fetch = FetchType.LAZY)
    private Association association;
}
