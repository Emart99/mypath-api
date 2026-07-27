package com.tramo.backend.project.dto;

import com.tramo.backend.trail.dto.AssociationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PublicItemDTO {
    private Long id;
    private String title;
    private String type;
    private String content;
    private String titleAlign;
    // Per-step metadata (this occurrence's position in its trail), for the trail view.
    private String annotation;
    private String associationId;
    // Outgoing ITEM-target associations, restricted to targets within this same
    // project (see ProjectService.getPublicProject) — powers the graph view.
    private List<AssociationDTO> associations;
}
