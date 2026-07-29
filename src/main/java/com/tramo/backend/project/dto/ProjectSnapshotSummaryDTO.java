package com.tramo.backend.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ProjectSnapshotSummaryDTO {
    private Long id;
    private int version;
    private Date createdDate;
}
