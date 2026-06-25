package com.example.lms_api.service.impl;



import com.example.lms_api.dto.request.StudentRequest;
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

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public StudentResponse getStudentByStudentId(Integer userId) {
        Student student = studentRepository.findByStudentId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thông tin sinh viên cho User ID: " + userId));

        return studentMapper.toStudentResponse(student);
    }

    @Override
    public StudentResponse updateStudent(Integer studentId, StudentRequest request) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm học viên với ID: " + studentId));

        // MapStruct sẽ bỏ qua field null nhờ IGNORE strategy
        studentMapper.updateEntityFromRequest(request, student);
        student = studentRepository.save(student);

        return studentMapper.toStudentResponse(student);
    }
}