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
    private String teacherName;
    private String categoryName;
    /** User hiện tại đã mua/được kích hoạt khóa học hay chưa */
    private Boolean purchased;

    /** Active | Pending | Suspended | Completed | CourseDeleted (theo Enrollment) */
    private String accessStatus;
    // Thống kê theo từng khóa học
    private long   enrollmentCount; // số học sinh enroll khóa này
    private double avgRating;       // rating trung bình khóa này
}