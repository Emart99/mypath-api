package com.tramo.backend.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequestDTO {
    private String bio;
    private LocalDate birthDate;
    private String location;
    private String website;
    private String imageUrl;
    private String bannerUrl;
}
