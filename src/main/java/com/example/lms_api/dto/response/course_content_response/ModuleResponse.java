package com.example.lms_api.dto.response.course_content_response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ModuleResponse {
    private String moduleId;
    private String title;
    private int orderIndex;
    private List<LessonResponse> lessons;
}
