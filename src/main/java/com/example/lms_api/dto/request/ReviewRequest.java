package com.example.lms_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class ReviewRequest {
    @NotNull(message = "Rating không được để trống")
    @Min(1) @Max(5)
    private Double rating;

    @NotBlank(message = "Tiêu đề đánh giá không được để trống")
    private String title;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    private String content;

    private List<String> pros;
    private List<String> cons;
    private Integer enrollmentId;
}