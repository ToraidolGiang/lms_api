package com.example.lms_api.dto.request.course_content_request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
 public class ModuleRequest {

    // Khi tạo mới để trống — server tự sinh ID theo format M001, M002...
    private String moduleId;

    @NotBlank(message = "Tiêu đề module không được để trống")
    private String title;

    @NotNull(message = "Thứ tự module không được để trống")
    private Integer orderIndex;

    @Valid
    private List<LessonRequest> lessons;
}
