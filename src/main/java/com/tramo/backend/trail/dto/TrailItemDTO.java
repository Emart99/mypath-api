package com.tramo.backend.trail.dto;

import java.util.Date;




public record TrailItemDTO(
        Long id,
        String title,
        String type,
        String titleAlign,
        Date createdDate,
        Date modifiedDate,
        String annotation,
        String associationId
) {
}
