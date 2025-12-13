package com.mypath.backend.path;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class IdeaContent {
    @Id
    private Integer id;
    private String content;
    private Date updatedDate;
}
