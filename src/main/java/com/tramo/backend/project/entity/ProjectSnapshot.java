package com.tramo.backend.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = @Index(name = "idx_project_snapshot_project", columnList = "project_id"))
public class ProjectSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // "PUBLISH" or "FORK" — which action produced this snapshot
    private String trigger;

    // Sequential per-project version number (v1, v2, ...) — only set for PUBLISH; null for FORK.
    private Integer version;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Date createdDate;
}
