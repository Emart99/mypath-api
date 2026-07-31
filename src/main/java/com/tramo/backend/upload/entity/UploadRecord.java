package com.tramo.backend.upload.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;


@Entity
@Getter @Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_upload_record_user", columnList = "userId"),
        @Index(name = "idx_upload_record_project", columnList = "projectId")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_upload_record_key", columnNames = "objectKey"))
public class UploadRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private Long projectId;
    private String objectKey;
    private long bytes;
    private Date createdDate;
}
