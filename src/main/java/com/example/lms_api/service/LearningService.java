package com.example.lms_api.service;

import com.example.lms_api.dto.request.learning.SubmitAssignmentRequest;
import com.example.lms_api.dto.request.learning.SubmitQuizRequest;
import com.example.lms_api.dto.request.learning.SyncVideoRequest;
import com.example.lms_api.dto.response.learning.ProgressResponse;

public interface LearningService {

    ProgressResponse getProgress(Integer courseId);

    void syncVideoProgress(Integer courseId, String lessonId, SyncVideoRequest request);

    // (Sau này thêm các hàm submitQuiz, submitAssignment vào đây)
    ProgressResponse submitQuiz(Integer courseId, String lessonId, SubmitQuizRequest request);
    ProgressResponse submitAssignment(Integer courseId, String lessonId, SubmitAssignmentRequest request);
}