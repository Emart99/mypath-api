package com.tramo.backend.trail.entity;

import com.tramo.backend.project.entity.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_trail_project", columnList = "project_id"),
})
public class Trail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String visibility;
    private Date creationDate;
    private Date modifiedDate;

    // Bumped when the owner publishes/iterates, so old versions aren't broken.
    @Column(nullable = false)
    private int version = 1;

    // Lineage: the original trail this one was forked from (null if not a fork).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forked_from_trail_id")
    private Trail forkedFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    @OneToMany(mappedBy = "trail")
    private List<TrailItem> trailItem;
}
