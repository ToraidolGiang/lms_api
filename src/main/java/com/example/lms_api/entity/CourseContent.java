package com.example.lms_api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses_content")
public class CourseContent {
    @Id
    private String id;
    private Integer courseId;
    private String courseTitle;
    private String description;
    private List<Module> modules;
    private Metadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Module {
        private String moduleId;
        private String title;
        private Integer orderIndex;
        private List<Lesson> lessons;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lesson {
        private String lessonId;
        private String title;
        private String type; // "video" hoặc "quiz"
        private Integer orderIndex;
        private Integer duration;
        private Object content;
        private Boolean isPreview;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private Integer totalLessons;
        private Integer totalDuration;
        private Date lastUpdated;
    }
}