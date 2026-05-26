package com.example.lms_api.service;


import com.example.lms_api.dto.request.course_content_request.CourseContentRequest;
import com.example.lms_api.dto.request.course_content_request.LessonRequest;
import com.example.lms_api.dto.request.course_content_request.ModuleRequest;
import com.example.lms_api.dto.response.course_content_response.CourseContentResponse;

public interface CourseContentService {

    // ── CRUD nội dung khóa học ────────────────────────────────

    // Tạo nội dung mới cho khóa học (lần đầu)
    CourseContentResponse createCourseContent(Integer courseId, CourseContentRequest request);

    // Lấy toàn bộ nội dung theo courseId
    CourseContentResponse getCourseContent(Integer courseId);

    // Cập nhật thông tin tổng quan (nameCourse, description...)
    CourseContentResponse updateCourseContent(Integer courseId, CourseContentRequest request);

    // Xóa toàn bộ nội dung khóa học
    void deleteCourseContent(Integer courseId);

    // ── CRUD Module ───────────────────────────────────────────

    CourseContentResponse addModule(Integer courseId, ModuleRequest request);

    CourseContentResponse updateModule(Integer courseId, String moduleId, ModuleRequest request);

    CourseContentResponse deleteModule(Integer courseId, String moduleId);

    // ── CRUD Lesson ───────────────────────────────────────────

    CourseContentResponse addLesson(Integer courseId, String moduleId, LessonRequest request);

    CourseContentResponse updateLesson(Integer courseId, String moduleId,
                                       String lessonId, LessonRequest request);

    CourseContentResponse deleteLesson(Integer courseId, String moduleId, String lessonId);
}