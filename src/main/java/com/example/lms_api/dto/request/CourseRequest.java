package com.example.lms_api.dto.request;


import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private Integer courseId; // Nếu tự điền bằng tay
    private Integer teacherId;
    private Integer categoryId;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal price;
}