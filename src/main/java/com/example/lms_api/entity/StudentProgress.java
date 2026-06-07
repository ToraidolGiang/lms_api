// entity/document/StudentProgress.java
package com.example.lms_api.entity.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "student_progress")
public class StudentProgress {
    @Id
    private String id;
    private Integer studentId;
    private Integer courseId;
    private Integer enrollmentId;
    private CourseProgressData progress;
    private List<LessonProgressData> lessonProgress;
    private LocalDateTime updatedAt;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CourseProgressData {
        @Builder.Default
        private List<String> completedLessons = new ArrayList<>();
        private String currentLesson;
        private Double overallProgress;
        private Integer totalWatchTime;
        private LocalDateTime lastAccessedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LessonProgressData {
        private String lessonId;
        private String status; // "in-progress", "completed"
        private Integer watchedDuration;
        private Integer totalDuration;
        private Double progressPercent;
        private Integer score; // Dành cho Quiz
        private String submissionUrl; // Dành cho Assignment
        private LocalDateTime lastAccessedAt;
        private LocalDateTime completedAt;
    }
}