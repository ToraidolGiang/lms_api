package com.example.lms_api.controller;

import com.example.lms_api.dto.request.learning.SubmitAssignmentRequest;
import com.example.lms_api.dto.request.learning.SubmitQuizRequest;
import com.example.lms_api.dto.request.learning.SyncVideoRequest;
import com.example.lms_api.dto.response.learning.ProgressResponse;
import com.example.lms_api.service.LearningService; // <-- Import interface
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learn/courses/{courseId}")
@RequiredArgsConstructor
public class LearningController {

    // SỬA DÒNG NÀY: Dùng Interface thay vì Impl
    private final LearningService learningService;

    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress(@PathVariable Integer courseId) {
        return ResponseEntity.ok(learningService.getProgress(courseId));
    }

    @PutMapping("/lessons/{lessonId}/sync")
    public ResponseEntity<Void> syncVideo(
            @PathVariable Integer courseId,
            @PathVariable String lessonId,
            @RequestBody SyncVideoRequest request) {
        learningService.syncVideoProgress(courseId, lessonId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lessons/{lessonId}/submit-quiz")
    public ResponseEntity<ProgressResponse> submitQuiz(
            @PathVariable Integer courseId,
            @PathVariable String lessonId,
            @RequestBody SubmitQuizRequest request) {
        return ResponseEntity.ok(learningService.submitQuiz(courseId, lessonId, request));
    }

    @PostMapping("/lessons/{lessonId}/submit-assignment")
    public ResponseEntity<ProgressResponse> submitAssignment(
            @PathVariable Integer courseId,
            @PathVariable String lessonId,
            @RequestBody SubmitAssignmentRequest request) {
        return ResponseEntity.ok(learningService.submitAssignment(courseId, lessonId, request));
    }

    @GetMapping("/lessons/{lessonId}/discussions")
    public ResponseEntity<java.util.List<com.example.lms_api.dto.response.learning.DiscussionResponse>> getDiscussions(
            @PathVariable Integer courseId,
            @PathVariable String lessonId) {
        return ResponseEntity.ok(learningService.getLessonDiscussions(courseId, lessonId));
    }

    @PostMapping("/lessons/{lessonId}/discussions")
    public ResponseEntity<com.example.lms_api.dto.response.learning.DiscussionResponse> createDiscussion(
            @PathVariable Integer courseId,
            @PathVariable String lessonId,
            @RequestBody com.example.lms_api.dto.request.learning.CreateDiscussionRequest request) {
        return ResponseEntity.ok(learningService.createDiscussion(courseId, lessonId, request));
    }
}