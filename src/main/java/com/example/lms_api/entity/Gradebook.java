package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gradebook")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Gradebook {
    @Id
    @Column(name = "gradeid", length = 50)
    private String gradeId;

    @Column(name = "submissionid", length = 50)
    private String submissionId;

    @Column(name = "score")
    private Double score;

    @Column(name = "ispassed")
    private Boolean isPassed;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "gradedat")
    private LocalDateTime gradedAt;

    @Column(name = "gradedby", length = 50)
    private String gradedBy;
}