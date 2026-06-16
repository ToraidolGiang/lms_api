package com.example.lms_api.dto.response.learning;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProgressResponse {
    private String currentLesson;
    private Double overallProgress;
    private Integer totalWatchTime;
    private List<String> completedLessons;
    private List<LessonDetailProgress> lessonDetails;

    @Data @Builder
    public static class LessonDetailProgress {
        private String lessonId;
        private String status;
        private Double progressPercent;
        private Integer score;
    }
}
