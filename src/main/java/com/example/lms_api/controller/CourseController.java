package com.example.lms_api.controller;
import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseContentResponse;
import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.service.CourseContentService;
import com.example.lms_api.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/courses", "/api/courses"})
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 1. [POST] Tạo khóa học mới
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request) {
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
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Integer id, @RequestBody CourseRequest request) {
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

    // Spring sẽ tự động inject CourseContentServiceImpl vào biến này
    private final CourseContentService courseContentService;

    @GetMapping("/{id}/content")
    public ResponseEntity<CourseContentResponse> getCourseContentById(@PathVariable Integer id) {
        CourseContentResponse response = courseContentService.getCourseContent(id);
        return ResponseEntity.ok(response);
    }
}