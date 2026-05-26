package com.example.lms_api.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseMetadata {

    @Field("totalLessons")
    private int totalLessons;

    @Field("totalDuration")
    private int totalDuration;

    @Field("lastUpdated")
    private LocalDateTime lastUpdated;
}