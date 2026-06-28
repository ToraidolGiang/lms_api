package com.example.lms_api.service;

import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.PagedResponse;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    List<CourseResponse> getAllActiveCourses();
    CourseResponse getCourseById(Integer id);
    CourseResponse updateCourse(Integer id, CourseRequest request);
    void deleteCourse(Integer id, String deletedBy, String reason);
    List<CourseResponse> getCourseByTeacherId(Integer teacherId);
    List<CourseResponse> getExploreCourses();
    List<CourseResponse> getExploreCoursesTea();

    /**
     * GET /api/v1/courses/explore?page=0&size=8
     * Phân trang danh sách khoá học explore dành cho student.
     */
    PagedResponse<CourseResponse> getExploreCoursesPagedStudent(int page, int size);

    /**
     * GET /api/v1/courses/explore/me?page=0&size=8
     * Phân trang danh sách khoá học explore dành cho teacher.
     */
    PagedResponse<CourseResponse> getExploreCoursesPagedTeacher(int page, int size);
}