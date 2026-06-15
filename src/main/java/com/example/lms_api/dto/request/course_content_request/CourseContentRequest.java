package com.example.lms_api.dto.request.course_content_request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

// ── CourseContentRequest ──────────────────────────────────────
@Data
public class CourseContentRequest {

    @NotBlank(message = "Tên khóa học không được để trống")
    private String nameCourse;

    private String courseTitle;
    private String description;

    @Valid
    private List<ModuleRequest> modules;
}
