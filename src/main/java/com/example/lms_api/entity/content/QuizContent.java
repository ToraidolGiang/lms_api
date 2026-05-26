package com.example.lms_api.entity.content;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizContent extends LessonContent {
    private int passScore;
    private List<QuizQuestion> questions;
}
