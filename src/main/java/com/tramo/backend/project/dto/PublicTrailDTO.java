package com.tramo.backend.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PublicTrailDTO {
    private Long id;
    private String title;
    private String description;
    private int version;
    private String forkedFromId;
    private List<PublicItemDTO> items;
}
