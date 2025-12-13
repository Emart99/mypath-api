package com.mypath.backend.path;

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
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String title;
    private String visibility;
    private Date creationDate;
    private Date modifiedDate;


    @OneToMany(mappedBy = "path")
    private List<PathIdea> pathIdea;
}
