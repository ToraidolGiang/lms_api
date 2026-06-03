package com.example.lms_api.service;

import com.example.lms_api.dto.request.StudentRequest;
import com.example.lms_api.dto.response.StudentResponse;

public interface StudentService {
    // Lấy thông tin chi tiết theo userID
    StudentResponse getStudentByStudentId(Integer userId);
    StudentResponse updateStudent(Integer stu, StudentRequest request);

}