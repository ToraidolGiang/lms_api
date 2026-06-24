package com.example.lms_api.mapper;

import com.example.lms_api.dto.request.CourseGradeRequest;
import com.example.lms_api.dto.response.CourseGradeResponse;
import com.example.lms_api.entity.CourseGrade;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseGradeMapper {

    @Mapping(target = "courseGradeId", ignore = true)
    @Mapping(target = "gradedAt",      ignore = true)
    CourseGrade toEntity(CourseGradeRequest request);

    CourseGradeResponse toResponse(CourseGrade entity);

    // Dùng cho Student: che giấu điểm nếu isMasked = true
    default CourseGradeResponse toResponseForStudent(CourseGrade entity) {
        if (Boolean.TRUE.equals(entity.getIsMasked())) {
            return CourseGradeResponse.builder()
                    .courseGradeId(entity.getCourseGradeId())
                    .studentId(entity.getStudentId())
                    .courseId(entity.getCourseId())
                    .gradedAt(entity.getGradedAt())
                    .isMasked(true)
                    // examScore, finalScore, gradeLevel, quizAvgScore → null (ẩn)
                    .build();
        }
        return toResponse(entity);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "courseGradeId", ignore = true)
    @Mapping(target = "gradedAt",      ignore = true)
    void updateEntityFromRequest(CourseGradeRequest request, @MappingTarget CourseGrade entity);
}