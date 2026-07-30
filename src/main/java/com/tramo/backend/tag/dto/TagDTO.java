package com.tramo.backend.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagDTO {
    private String name;
    private boolean official;
    private long usageCount;
}
