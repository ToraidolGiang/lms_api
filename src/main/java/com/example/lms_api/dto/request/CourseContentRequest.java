package com.example.lms_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseContentRequest {
    private Integer courseId;
    private String courseTitle;
    private String description;
    private List<ModuleRequest> modules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleRequest {
        private String title;
        private Integer orderIndex;
        private List<LessonRequest> lessons;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonRequest {
        private String title;
        private String type;
        private Integer orderIndex;
        private Integer duration;
        private Object content;
        private Boolean isPreview;
    }
}