package com.example.lms_api.controller;

import com.example.lms_api.dto.request.CourseGradeRequest;
import com.example.lms_api.dto.request.SubmitGradeRequest;
import com.example.lms_api.dto.response.CourseGradeResponse;
import com.example.lms_api.dto.response.GradingListResponse;
import com.example.lms_api.service.CourseGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course-grades")
@RequiredArgsConstructor
public class CourseGradeController {

    private final CourseGradeService courseGradeService;

    // ── CRUD cũ ──────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<CourseGradeResponse> create(@RequestBody CourseGradeRequest request) {
        return new ResponseEntity<>(courseGradeService.createGrade(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseGradeResponse> update(
            @PathVariable Integer id,
            @RequestBody CourseGradeRequest request) {
        return ResponseEntity.ok(courseGradeService.updateGrade(id, request));
    }

    // PUT /api/v1/course-grades/5/mask?masked=true
    @PutMapping("/{id}/mask")
    public ResponseEntity<CourseGradeResponse> toggleMask(
            @PathVariable Integer id,
            @RequestParam Boolean masked) {
        return ResponseEntity.ok(courseGradeService.toggleMask(id, masked));
    }

    // TEACHER/ADMIN — thấy cả điểm đang bị ẩn
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseGradeResponse>> getByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseGradeService.getGradesByCourse(courseId));
    }

    // STUDENT — điểm bị ẩn → null
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseGradeResponse>> getByStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(courseGradeService.getGradesByStudentForStudent(studentId));
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submitOrUpdate(@RequestBody CourseGradeRequest request) {
        courseGradeService.submitOrUpdateGrade(request);
        return ResponseEntity.ok().build();
    }

    // ── Grading feature ──────────────────────────────────────────

    /**
     * GET /api/v1/course-grades/grading/{courseId}
     * TEACHER: Danh sách sinh viên + link bài nộp + thống kê
     */
    @GetMapping("/grading/{courseId}")
    public ResponseEntity<GradingListResponse> getGradingList(@PathVariable Integer courseId) {
        return ResponseEntity.ok(courseGradeService.getGradingList(courseId));
    }

    /**
     * POST /api/v1/course-grades/grading/submit
     * TEACHER: Nhập examScore → tính quizAvg, finalScore, gradeLevel → ghi CourseGrade
     * Body: { studentId, courseId, examScore, isMasked }
     */
    @PostMapping("/grading/submit")
    public ResponseEntity<GradingListResponse.StudentGradingItem> submitGrade(
            @RequestBody SubmitGradeRequest request) {
        return ResponseEntity.ok(courseGradeService.submitGrade(request));
    }
}