package com.example.lms_api.dto.request.learning;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitQuizRequest {
    // Key: ID câu hỏi (VD: "q1"), Value: Đáp án học viên chọn (VD: "A" hoặc "True")
    private Map<String, String> studentAnswers;
}