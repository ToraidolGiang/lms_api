package com.example.lms_api.mapper;

import com.example.lms_api.dto.request.TeacherRequest;
import com.example.lms_api.dto.response.TeacherResponse;
import com.example.lms_api.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeacherMapper {

    // 1. Map từ Entity sang Response
    @Mapping(source = "user.id", target = "userId")
    TeacherResponse toResponse(Teacher entity);

    // 2. Map từ Request sang Entity
    @Mapping(target = "teacherId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Teacher toEntity(TeacherRequest request);

    // 3. Map từ Request đè lên Entity cũ
    @Mapping(target = "teacherId", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequest(TeacherRequest request, @MappingTarget Teacher entity);
}