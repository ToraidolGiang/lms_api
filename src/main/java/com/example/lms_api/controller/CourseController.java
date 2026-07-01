package com.example.lms_api.controller;
import jakarta.validation.Valid;
import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.PagedResponse;
import com.example.lms_api.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 1. [POST] Tạo khóa học mới
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return new ResponseEntity<>(courseService.createCourse(request), HttpStatus.CREATED);
    }

    // 2. [GET] Lấy danh sách khóa học đang hoạt động
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllActiveCourses());
    }

    // 3. [GET] Lấy chi tiết 1 khóa học bằng ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("teacher/{id}")
    public ResponseEntity<List<CourseResponse>> getCourseByTeacherId(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCourseByTeacherId(id));
    }
    // 4. [PUT] Cập nhật thông tin khóa học
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Integer id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    // 5. [DELETE] Xóa mềm khóa học
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "Admin") String deletedBy,
            @RequestParam(defaultValue = "Không dùng nữa") String reason) {
        courseService.deleteCourse(id, deletedBy, reason);
        return ResponseEntity.ok("Xóa mềm khóa học thành công!");
    }

    // 6. [PATCH] Khôi phục khóa học đã xóa mềm
    @PatchMapping("/{id}/restore")
    public ResponseEntity<String> restoreCourse(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "Admin") String restoredBy) {
        courseService.restoreCourse(id, restoredBy);
        return ResponseEntity.ok("Khôi phục khóa học thành công!");
    }

    @GetMapping("/explore")
    public ResponseEntity<List<CourseResponse>> getExploreCourses() {
        return ResponseEntity.ok(courseService.getExploreCourses());
    }

    @GetMapping("/explore/me")
    public ResponseEntity<List<CourseResponse>> getExploreCoursesTea() {
        return ResponseEntity.ok(courseService.getExploreCoursesTea());
    }

    /**
     * GET /api/v1/courses/explore/paged?page=0&size=8
     * Phân trang danh sách khoá học explore (student).
     */
    @GetMapping("/explore/paged")
    public ResponseEntity<PagedResponse<CourseResponse>> getExploreCoursesPagedStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String rating) {
        if (size > 50) size = 50;
        return ResponseEntity.ok(courseService.getExploreCoursesPagedStudent(page, size, search, category, price, rating));
    }

    /**
     * GET /api/v1/courses/explore/me/paged?page=0&size=8
     * Phân trang danh sách khoá học explore (teacher).
     */
    @GetMapping("/explore/me/paged")
    public ResponseEntity<PagedResponse<CourseResponse>> getExploreCoursesPagedTeacher(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String rating) {
        if (size > 50) size = 50;
        return ResponseEntity.ok(courseService.getExploreCoursesPagedTeacher(page, size, search, category, price, rating));
    }
}