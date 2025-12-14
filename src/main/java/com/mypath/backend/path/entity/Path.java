package com.mypath.backend.path.entity;

import com.mypath.backend.user.entity.User;
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
    private Long id;
    private String title;
    private String visibility;
    private Date creationDate;
    private Date modifiedDate;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User owner;

    @OneToMany(mappedBy = "path")
    private List<PathIdea> pathIdea;
}
