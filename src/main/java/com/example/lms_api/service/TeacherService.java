package com.example.lms_api.service;
import com.example.lms_api.dto.request.TeacherRequest;
import com.example.lms_api.dto.response.TeacherResponse;

public interface TeacherService {
    // Lấy thông tin chi tiết một giáo viên theo ID
    TeacherResponse findByTeacherId(Integer teacherId);


    // Cập nhật thông tin giáo viên (Dùng MapStruct đè dữ liệu)
    TeacherResponse updateTeacher(Integer teacherId, TeacherRequest request);

}
