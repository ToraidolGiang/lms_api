package com.example.lms_api.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TeacherDashboardResponse {

    // ── Stats cards (Số liệu thực tế tính toán) ──────────
    private long   totalStudents;
    private long   enrollmentsThisMonth;
    private BigDecimal totalRevenue;
    private double avgRating;
    private long   totalCourses;

    // ── CÁC TRƯỜNG BỔ SUNG: Dùng để hiển thị chữ phụ trợ lên UI Android ──
    private String earningsChange;
    private String studentsChange;
    private String revenueChange;
    private String ratingChange;

    // ── Danh sách khóa học ───────────────────────────────
    private List<CourseResponse> courses;
}