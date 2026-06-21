package com.example.lms_api.controller;

import com.example.lms_api.dto.request.TeacherRequest;
import com.example.lms_api.dto.response.TeacherResponse;
import com.example.lms_api.service.impl.TeacherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherServiceImpl teacherService;

    /**
     * Lấy thông tin chi tiết một giáo viên theo ID
     * Method: GET
     * URL: /api/teachers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable("id") Integer teacherId) {
        TeacherResponse response = teacherService.findByTeacherId(teacherId);
        return ResponseEntity.ok(response); // Trả về HTTP Status 200 OK
    }

    /**
     * Cập nhật thông tin giáo viên
     * Method: PUT (hoặc có thể dùng PATCH cho partial update)
     * URL: /api/teachers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable("id") Integer teacherId,
            @RequestBody TeacherRequest request) {

        TeacherResponse response = teacherService.updateTeacher(teacherId, request);
        return ResponseEntity.ok(response); // Trả về HTTP Status 200 OK kèm dữ liệu đã cập nhật
    }
}