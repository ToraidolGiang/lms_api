package com.example.lms_api.service.impl;



import com.example.lms_api.dto.request.TeacherRequest;
import com.example.lms_api.dto.response.StudentResponse;
import com.example.lms_api.entity.Student;
import com.example.lms_api.mapper.StudentMapper;
import com.example.lms_api.repository.StudentRepository;
import com.example.lms_api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentResponse updateStudent(Integer stu, TeacherRequest request) {
        return null;
    }

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public StudentResponse getStudentById(Integer userId) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thông tin sinh viên cho User ID: " + userId));

        return studentMapper.toStudentResponse(student);
    }
}