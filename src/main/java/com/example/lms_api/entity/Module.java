package com.example.lms_api.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Module {

    @Field("moduleId")
    private String moduleId;

    @Field("title")
    private String title;

    @Field("orderIndex")
    private int orderIndex;

    @Field("lessons")
    private List<Lesson> lessons;
}