package com.example.lms_api.entity.content;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoContent extends LessonContent {
    private String videoId;
}