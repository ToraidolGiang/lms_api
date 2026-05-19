package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.TeacherRequest;
import com.example.lms_api.dto.response.TeacherResponse;
import com.example.lms_api.entity.Teacher;
import com.example.lms_api.mapper.TeacherMapper;
import com.example.lms_api.repository.TeacherRepository;
import com.example.lms_api.repository.UserRepository;
import com.example.lms_api.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final UserRepository userRepository;


    // 2. Hàm lấy theo ID
    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Integer teacherId) {
        Teacher teacher = teacherRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        return teacherMapper.toResponse(teacher);
    }



    // 4. Hàm cập nhật
    @Override
    @Transactional
    public TeacherResponse updateTeacher(Integer teacherId, TeacherRequest request) {
        Teacher teacher = teacherRepository.findByTeacherId(teacherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));

        teacherMapper.updateEntityFromRequest(request, teacher);
        teacher = teacherRepository.save(teacher);

        return teacherMapper.toResponse(teacher);
    }

}