// mapper/LearningMapper.java
package com.example.lms_api.mapper;

import com.example.lms_api.dto.response.learning.ProgressResponse;
import com.example.lms_api.entity.document.StudentProgress;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class LearningMapper {
    public ProgressResponse toResponse(StudentProgress entity) {
        if (entity == null) return null;

        return ProgressResponse.builder()
                .currentLesson(entity.getProgress().getCurrentLesson())
                .overallProgress(entity.getProgress().getOverallProgress())
                .totalWatchTime(entity.getProgress().getTotalWatchTime())
                .completedLessons(entity.getProgress().getCompletedLessons())
                .lessonDetails(entity.getLessonProgress().stream()
                        .map(lp -> ProgressResponse.LessonDetailProgress.builder()
                                .lessonId(lp.getLessonId())
                                .status(lp.getStatus())
                                .progressPercent(lp.getProgressPercent())
                                .score(lp.getScore())
                                .attemptCount(lp.getAttemptCount())
                                .maxAttempts(lp.getMaxAttempts())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}