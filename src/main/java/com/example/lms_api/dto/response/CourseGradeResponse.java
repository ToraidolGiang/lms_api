package com.example.lms_api.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseGradeResponse {
    private Integer courseGradeId;
    private Integer studentId;
    private Integer courseId;
    private BigDecimal examScore;
    private BigDecimal finalScore;
    private String gradeLevel;
    private LocalDateTime gradedAt;
    private BigDecimal quizAvgScore;
    private Boolean isMasked; // TEACHER/ADMIN thấy; Student không thấy field này
}