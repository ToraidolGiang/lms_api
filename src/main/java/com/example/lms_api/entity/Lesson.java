package com.example.lms_api.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lesson {

    @Field("lessonId")
    private String lessonId;

    @Field("title")
    private String title;

    // "video" | "quiz" | "assignment"
    @Field("type")
    private String type;

    @Field("orderIndex")
    private int orderIndex;

    // Thời lượng tính bằng giây
    @Field("duration")
    private int duration;

    // Dùng Map<String, Object> để lưu content linh hoạt
    // vì mỗi type có cấu trúc content khác nhau
    @Field("content")
    private Map<String, Object> content;

    // Chỉ có ở lesson type=video
    @Field("isPreview")
    private Boolean isPreview;
}