package com.example.lms_api.service.impl;

import com.example.lms_api.dto.response.CourseContentResponse;
import com.example.lms_api.entity.CourseContent;
import com.example.lms_api.repository.CourseContentRepository;
import com.example.lms_api.service.CourseContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseContentServiceImpl implements CourseContentService {

    private final CourseContentRepository contentRepository;

    @Override
    public CourseContentResponse getCourseContent(Integer courseId) {
        // Lấy Entity từ MongoDB
        CourseContent entity = contentRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nội dung cho khóa học: " + courseId));

        // Map từ Entity sang Response DTO (Dùng Builder pattern)
        return CourseContentResponse.builder()
                .courseId(entity.getCourseId())
                .courseTitle(entity.getCourseTitle())
                .description(entity.getDescription())
                .metadata(CourseContentResponse.MetadataResponse.builder()
                        .totalLessons(entity.getMetadata().getTotalLessons())
                        .totalDuration(entity.getMetadata().getTotalDuration())
                        .lastUpdated(entity.getMetadata().getLastUpdated())
                        .build())
                .modules(entity.getModules().stream().map(m -> CourseContentResponse.ModuleResponse.builder()
                        .moduleId(m.getModuleId())
                        .title(m.getTitle())
                        .orderIndex(m.getOrderIndex())
                        .lessons(m.getLessons().stream().map(l -> CourseContentResponse.LessonResponse.builder()
                                .lessonId(l.getLessonId())
                                .title(l.getTitle())
                                .type(l.getType())
                                .orderIndex(l.getOrderIndex())
                                .duration(l.getDuration())
                                .content(l.getContent())
                                .isPreview(l.getIsPreview())
                                .build()).toList())
                        .build()).toList())
                .build();
    }
}