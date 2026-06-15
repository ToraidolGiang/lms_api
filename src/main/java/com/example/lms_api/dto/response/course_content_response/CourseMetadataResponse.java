package com.example.lms_api.dto.response.course_content_response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
 public class CourseMetadataResponse {
    private int totalLessons;
    private int totalDuration;
    private LocalDateTime lastUpdated;
}
