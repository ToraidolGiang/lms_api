package com.example.lms_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseContentResponse {
    private Integer courseId;
    private String courseTitle;
    private String description;
    private List<ModuleResponse> modules;
    private MetadataResponse metadata;

    // Bê các class con vào trong này và thêm 'public static'
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ModuleResponse {
        private String moduleId;
        private String title;
        private Integer orderIndex;
        private List<LessonResponse> lessons;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LessonResponse {
        private String lessonId;
        private String title;
        private String type;
        private Integer orderIndex;
        private Integer duration;
        private Object content;
        private Boolean isPreview;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MetadataResponse {
        private Integer totalLessons;
        private Integer totalDuration;
        private Date lastUpdated;
    }
}