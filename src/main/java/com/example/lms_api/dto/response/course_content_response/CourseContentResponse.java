package com.example.lms_api.dto.response.course_content_response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseContentResponse {
    private String id;
    private Integer courseId;
    private String nameCourse;
    private String courseTitle;
    private String description;
    private List<ModuleResponse> modules;
    private CourseMetadataResponse metadata;
}
