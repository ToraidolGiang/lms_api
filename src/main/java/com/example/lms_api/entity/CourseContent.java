package com.example.lms_api.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "courses_content")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseContent {

    @Id
    private String id; // MongoDB ObjectId

    // Liên kết với bảng Courses bên PostgreSQL
    @Indexed(unique = true)
    @Field("courseId")
    private Integer courseId;

    @Field("nameCourse")
    private String nameCourse;

    @Field("courseTitle")
    private String courseTitle;

    @Field("description")
    private String description;

    @Field("modules")
    private List<Module> modules;

    @Field("metadata")
    private CourseMetadata metadata;
}