package com.example.lms_api.dto.request.course_content_request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LessonRequest {

    // Khi tạo mới để trống — server tự sinh ID theo format M001_L001
    private String lessonId;

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String title;

    @NotBlank(message = "Loại bài học không được để trống (video/quiz/assignment)")
    private String type; // "video" | "quiz" | "assignment"

    @NotNull(message = "Thứ tự bài học không được để trống")
    private Integer orderIndex;

    private Integer duration; // giây, mặc định 0

    // Dùng Map để linh hoạt với 3 loại content khác nhau
    private Map<String, Object> content;

    private Boolean isPreview; // chỉ dùng cho video
}