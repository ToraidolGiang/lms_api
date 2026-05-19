package com.example.lms_api.mapper;

import com.example.lms_api.dto.request.StudentRequest;
import com.example.lms_api.dto.request.TeacherRequest;
import com.example.lms_api.dto.response.StudentResponse;
import com.example.lms_api.entity.Student;
import com.example.lms_api.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// componentModel = "spring" giúp bạn có thể @Autowired mapper này vào Service
@Mapper(componentModel = "spring")
public interface StudentMapper {

    // 1. Chuyển từ Entity (Student) sang DTO (StudentResponse)
    // Cần chỉ định lấy ID từ object User bên trong Student để gán cho userId của Response
    @Mapping(source = "user.id", target = "userId")
    StudentResponse toStudentResponse(Student student);

    // 2. Chuyển từ DTO (StudentRequest) sang Entity (Student)
    // Cần chỉ định lấy userId từ Request để tạo ra object User (có id) bên trong Student
//    @Mapping(source = "userId", target = "user.id")
//    Student toStudent(StudentRequest request);
//
//    @Mapping(target = "studentId", ignore = true)
//    @Mapping(target = "user", ignore = true)
//    Student toStudent(StudentRequest request);

}