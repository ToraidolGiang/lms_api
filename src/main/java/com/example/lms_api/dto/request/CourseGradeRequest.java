package com.example.lms_api.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseGradeRequest {
    private Integer studentId;
    private Integer courseId;
    private BigDecimal examScore;
    private BigDecimal finalScore;
    private String gradeLevel;
    private BigDecimal quizAvgScore;
    private Boolean isMasked; // TEACHER/ADMIN set khi tạo hoặc cập nhật
}