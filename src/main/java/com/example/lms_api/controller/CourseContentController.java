package com.example.lms_api.controller;


import com.example.lms_api.dto.request.course_content_request.CourseContentRequest;
import com.example.lms_api.dto.request.course_content_request.LessonRequest;
import com.example.lms_api.dto.request.course_content_request.ModuleRequest;
import com.example.lms_api.dto.response.course_content_response.CourseContentResponse;
import com.example.lms_api.service.CourseContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/content")
@RequiredArgsConstructor
public class CourseContentController {

    private final CourseContentService courseContentService;

    // ── CRUD Course Content ───────────────────────────────────

    // POST /api/v1/courses/5/content
    // Tạo nội dung lần đầu cho khóa học
    @PostMapping
    public ResponseEntity<CourseContentResponse> createContent(
            @PathVariable Integer courseId,
            @Valid @RequestBody CourseContentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseContentService.createCourseContent(courseId, request));
    }

    // GET /api/v1/courses/5/content
    // Lấy toàn bộ nội dung khóa học
    @GetMapping
    public ResponseEntity<CourseContentResponse> getContent(
            @PathVariable Integer courseId) {
        return ResponseEntity.ok(courseContentService.getCourseContent(courseId));
    }

    // PUT /api/v1/courses/5/content
    // Cập nhật thông tin tổng quan + modules
    @PutMapping
    public ResponseEntity<CourseContentResponse> updateContent(
            @PathVariable Integer courseId,
            @Valid @RequestBody CourseContentRequest request) {
        return ResponseEntity.ok(courseContentService.updateCourseContent(courseId, request));
    }

    // DELETE /api/v1/courses/5/content
    // Xóa toàn bộ nội dung
    @DeleteMapping
    public ResponseEntity<Void> deleteContent(
            @PathVariable Integer courseId) {
        courseContentService.deleteCourseContent(courseId);
        return ResponseEntity.noContent().build();
    }

    // ── CRUD Module ───────────────────────────────────────────

    // POST /api/v1/courses/5/content/modules
    @PostMapping("/modules")
    public ResponseEntity<CourseContentResponse> addModule(
            @PathVariable Integer courseId,
            @Valid @RequestBody ModuleRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseContentService.addModule(courseId, request));
    }

    // PUT /api/v1/courses/5/content/modules/M001
    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<CourseContentResponse> updateModule(
            @PathVariable Integer courseId,
            @PathVariable String moduleId,
            @Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(courseContentService.updateModule(courseId, moduleId, request));
    }

    // DELETE /api/v1/courses/5/content/modules/M001
    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<CourseContentResponse> deleteModule(
            @PathVariable Integer courseId,
            @PathVariable String moduleId) {
        return ResponseEntity.ok(courseContentService.deleteModule(courseId, moduleId));
    }

    // ── CRUD Lesson ───────────────────────────────────────────

    // POST /api/v1/courses/5/content/modules/M001/lessons
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<CourseContentResponse> addLesson(
            @PathVariable Integer courseId,
            @PathVariable String moduleId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(courseContentService.addLesson(courseId, moduleId, request));
    }

    // PUT /api/v1/courses/5/content/modules/M001/lessons/M001_L001
    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<CourseContentResponse> updateLesson(
            @PathVariable Integer courseId,
            @PathVariable String moduleId,
            @PathVariable String lessonId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(
                courseContentService.updateLesson(courseId, moduleId, lessonId, request));
    }

    // DELETE /api/v1/courses/5/content/modules/M001/lessons/M001_L001
    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<CourseContentResponse> deleteLesson(
            @PathVariable Integer courseId,
            @PathVariable String moduleId,
            @PathVariable String lessonId) {
        return ResponseEntity.ok(
                courseContentService.deleteLesson(courseId, moduleId, lessonId));
    }
}