package com.example.lms_api.mapper;

import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {

    // 1. Entity → Response
    @Mapping(source = "category.categoryName", target = "categoryName")
    // @Mapping(source = "teacher.fullName", target = "teacherName") // bỏ comment khi có TeacherEntity
    CourseResponse toResponse(Course entity);

    // 2. Request → Entity (tạo mới)
    @Mapping(target = "courseId",     ignore = true)
    @Mapping(target = "category",     ignore = true) // set thủ công trong Service
    @Mapping(target = "teacher",      ignore = true) // set thủ công trong Service
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "deletedAt",    ignore = true)
    @Mapping(target = "deletedBy",    ignore = true)
    @Mapping(target = "deleteReason", ignore = true)
    @Mapping(target = "isActive",     ignore = true)
    @Mapping(target = "isDeleted",    ignore = true)
    @Mapping(target = "archiveStatus",ignore = true)
    Course toEntity(CourseRequest request);

    // 3. Request đè lên Entity cũ (cập nhật)
    @Mapping(target = "courseId",     ignore = true)
    @Mapping(target = "category",     ignore = true)
    @Mapping(target = "teacher",      ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "deletedAt",    ignore = true)
    @Mapping(target = "deletedBy",    ignore = true)
    @Mapping(target = "deleteReason", ignore = true)
    @Mapping(target = "isActive",     ignore = true)
    @Mapping(target = "isDeleted",    ignore = true)
    @Mapping(target = "archiveStatus",ignore = true)
    void updateEntityFromRequest(CourseRequest request, @MappingTarget Course entity);
}