package com.example.lms_api.mapper;


import com.example.lms_api.dto.request.course_content_request.CourseContentRequest;
import com.example.lms_api.dto.request.course_content_request.LessonRequest;
import com.example.lms_api.dto.request.course_content_request.ModuleRequest;
import com.example.lms_api.dto.response.course_content_response.CourseContentResponse;
import com.example.lms_api.dto.response.course_content_response.CourseMetadataResponse;
import com.example.lms_api.dto.response.course_content_response.LessonResponse;
import com.example.lms_api.dto.response.course_content_response.ModuleResponse;
import com.example.lms_api.entity.CourseContent;
import com.example.lms_api.entity.CourseMetadata;
import com.example.lms_api.entity.Lesson;
import com.example.lms_api.entity.Module;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseContentMapper {

    // ── Entity → Response ─────────────────────────────────────
    CourseContentResponse toResponse(CourseContent entity);

    ModuleResponse toModuleResponse(Module module);

    LessonResponse toLessonResponse(Lesson lesson);

    CourseMetadataResponse toMetadataResponse(CourseMetadata metadata);

    List<ModuleResponse> toModuleResponseList(List<Module> modules);

    // ── Request → Entity (tạo mới) ────────────────────────────
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "courseId", ignore = true) // set thủ công trong Service
    @Mapping(target = "metadata", ignore = true) // tính toán trong Service
    CourseContent toEntity(CourseContentRequest request);

    @Mapping(target = "moduleId", ignore = true) // sinh trong Service
    @Mapping(target = "lessons",  ignore = true) // xử lý riêng
    Module toModule(ModuleRequest request);

    @Mapping(target = "lessonId", ignore = true) // sinh trong Service
    Lesson toLesson(LessonRequest request);

    // ── Request đè lên Entity cũ (cập nhật) ──────────────────
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "modules",  ignore = true) // xử lý riêng trong Service
    void updateEntityFromRequest(CourseContentRequest request,
                                 @MappingTarget CourseContent entity);
}