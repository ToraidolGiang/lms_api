package com.example.lms_api.entity.content;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private int id;
    private String question;
    private List<String> options;
    private int correctAnswer;
}