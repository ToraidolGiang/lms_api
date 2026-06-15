package com.example.lms_api.service;

import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.TeacherDashboardResponse;

import java.util.List;

public interface TeacherDashboardService {

    /**
     * Trả về toàn bộ dữ liệu dashboard:
     * stats + danh sách khoá học + 5 review gần nhất
     *
     * @param userId  — userId từ JWT (subject), là id trong bảng users
     */
    TeacherDashboardResponse getDashboard(Integer userId);

    /**
     * Chỉ lấy danh sách khoá học của teacher (tab My Courses)
     */
    List<CourseResponse> getMyCourses(Integer userId);
}