package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id", nullable = false, updatable = false)
    private Integer enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "enroll_date")
    private LocalDateTime enrollDate;

    @Column(name = "access_status", length = 20)
    private String accessStatus;

    @Column(name = "access_expiry_date")
    private LocalDateTime accessExpiryDate;

    @Column(name = "can_access_after_deletion")
    private Boolean canAccessAfterDeletion;
}
