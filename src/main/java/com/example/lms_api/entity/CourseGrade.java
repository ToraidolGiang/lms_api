package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CourseGrade")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseGradeID")
    private Integer courseGradeId;

    @Column(name = "StudentID")
    private Integer studentId;

    @Column(name = "CourseID")
    private Integer courseId;

    @Column(name = "ExamScore", precision = 5, scale = 2)
    private BigDecimal examScore;

    @Column(name = "FinalScore", precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Column(name = "GradeLevel", length = 10)
    private String gradeLevel;

    @Column(name = "GradedAt")
    private LocalDateTime gradedAt;

    @Column(name = "QuizAvgScore", precision = 5, scale = 2)
    private BigDecimal quizAvgScore;

    @Column(name = "IsMasked", nullable = false)
    @Builder.Default
    private Boolean isMasked = false;
}