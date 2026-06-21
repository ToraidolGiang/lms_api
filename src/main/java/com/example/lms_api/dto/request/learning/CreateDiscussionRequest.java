package com.example.lms_api.dto.request.learning;

import lombok.Data;
import java.util.List;

@Data
public class CreateDiscussionRequest {
    private String title;
    private String content;
    private String codeSnippet;
    private List<String> tags;
}