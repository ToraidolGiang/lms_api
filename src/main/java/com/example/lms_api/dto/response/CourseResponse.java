package com.example.lms_api.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Integer courseId;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private String archiveStatus;
    private String teacherName;  // Trả thêm tên cho tiện hiển thị
    private String categoryName; // Trả thêm tên danh mục
}