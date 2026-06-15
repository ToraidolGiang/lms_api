package com.example.lms_api.entity.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter @Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = VideoContent.class, name = "video"),
        @JsonSubTypes.Type(value = QuizContent.class, name = "quiz"),
        @JsonSubTypes.Type(value = AssignmentContent.class, name = "assignment")
})
public abstract class LessonContent {}