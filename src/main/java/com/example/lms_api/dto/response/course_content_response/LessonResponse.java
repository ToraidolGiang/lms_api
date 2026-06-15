package com.example.lms_api.dto.response.course_content_response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
 public class LessonResponse {
    private String lessonId;
    private String title;
    private String type;
    private int orderIndex;
    private int duration;
    private Map<String, Object> content;
    private Boolean isPreview;
}
