package com.example.lms_api.controller;

import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.TeacherDashboardResponse;
import com.example.lms_api.service.TeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService dashboardService;

    /**
     * GET /api/teachers/{userId}/dashboard
     *
     * Trả về toàn bộ dữ liệu cho màn hình TeacherHome:
     * - Stats: totalStudents, enrollmentsThisMonth, totalRevenue, avgRating, totalCourses
     * - Danh sách khoá học
     * - 5 review gần nhất
     *
     * Android gọi: userId lấy từ SharedPreferences (lưu sau login)
     */
    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<TeacherDashboardResponse> getDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(dashboardService.getDashboard(userId));
    }

    /**
     * GET /api/teachers/{userId}/courses
     *
     * Chỉ lấy danh sách khoá học — dùng cho tab "My Courses"
     */
    @GetMapping("/{userId}/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(dashboardService.getMyCourses(userId));
    }
}