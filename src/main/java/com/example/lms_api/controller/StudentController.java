package com.example.lms_api.controller;

import com.example.lms_api.dto.request.StudentRequest;
import com.example.lms_api.dto.response.StudentResponse;
import com.example.lms_api.service.StudentService;
import com.example.lms_api.service.impl.StudentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students") // Đường dẫn gốc cho toàn bộ API trong file này
@RequiredArgsConstructor // Tự động Inject StudentService nhờ lombok
public class StudentController {

    private final StudentServiceImpl studentService;

    // 1. LẤY CHI TIẾT THEO STUDENT ID (GET: /api/students/{id})
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Integer id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }
}
