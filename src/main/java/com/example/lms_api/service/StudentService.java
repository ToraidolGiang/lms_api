package com.example.lms_api.service;

import com.example.lms_api.dto.request.StudentRequest;
import com.example.lms_api.dto.request.TeacherRequest;
import com.example.lms_api.dto.response.StudentResponse;
import com.example.lms_api.dto.response.TeacherResponse;

import java.util.List;
public interface StudentService {
    // Lấy thông tin chi tiết theo userID
    StudentResponse getStudentById(Integer userId);
    StudentResponse updateStudent(Integer stu, TeacherRequest request);
}