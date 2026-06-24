package com.example.lms_api.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GradingListResponse {

    private GradingStats stats;
    private List<StudentGradingItem> students;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GradingStats {
        private int totalStudents;
        private int gradedCount;      // đã có examScore
        private int ungradedCount;    // chưa có examScore
        private int maskedCount;      // đang bị ẩn
        private Double avgFinalScore;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StudentGradingItem {
        private Integer studentId;
        private String  fullName;

        // Trạng thái
        private boolean graded;       // true = đã có examScore
        private Boolean isMasked;

        // Submission (bài nộp mới nhất, type = assignment)
        private String  submissionId;
        private String  fileUrl;
        private LocalDateTime submittedAt;

        // Điểm (null nếu chưa chấm)
        private Integer courseGradeId; // để PUT /course-grades/{id}
        private BigDecimal examScore;
        private BigDecimal quizAvgScore;
        private BigDecimal finalScore;
        private String  gradeLevel;
        private LocalDateTime gradedAt;
    }
}