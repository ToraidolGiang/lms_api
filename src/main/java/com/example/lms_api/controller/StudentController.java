package com.example.lms_api.controller;

import com.example.lms_api.dto.request.StudentRequest;
import com.example.lms_api.dto.response.StudentResponse;
import com.example.lms_api.service.impl.StudentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students") // Đường dẫn gốc cho toàn bộ API trong file này
@RequiredArgsConstructor // Tự động Inject StudentService nhờ lombok
public class StudentController {

    private final StudentServiceImpl studentService;

    // 1. LẤY CHI TIẾT THEO STUDENT ID (GET: /api/students/{id})
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Integer id) {
        StudentResponse response = studentService.getStudentByStudentId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Integer id, @RequestBody StudentRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }
}
