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
    
    /** Đánh dấu xem khóa học có đang bị xóa mềm hay không */
    private Boolean isDeleted;

    private Integer totalStudents;
    private Integer totalLessons;
    private double averageRating;
    /** Active | Pending | Suspended | Completed | CourseDeleted (theo Enrollment) */
    private String accessStatus;
    // Thống kê theo từng khóa học
}