package com.example.lms_api.controller;

import com.example.lms_api.dto.request.ReviewRequest;
import com.example.lms_api.dto.request.VoteRequest;
import com.example.lms_api.dto.response.ReviewResponse;
import com.example.lms_api.service.CourseReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService reviewService;

    // 1. Endpoint lấy toàn bộ review của một khóa học (Phục vụ Tab Reviews trên Android)
    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getCourseReviews(@PathVariable Integer courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourseId(courseId));
    }

    // 2. Endpoint viết một review mới cho khóa học
    // Đừng quên tích hợp lấy studentId từ Principal/Token JWT để bảo mật
    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Integer courseId,
            @RequestParam Integer studentId, // Có thể lấy tự động qua JWT Custom Principal
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.createReview(courseId, studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 3. Endpoint cập nhật lượt vote (Upvote / Downvote)
    @PutMapping("/{courseId}/reviews/{reviewId}/vote")
    public ResponseEntity<ReviewResponse> voteReview(
            @PathVariable Integer courseId, // Giữ lại cho đồng nhất URL
            @PathVariable String reviewId,
            @Valid @RequestBody VoteRequest request) {

        ReviewResponse response = reviewService.voteReview(reviewId, request);
        return ResponseEntity.ok(response);
    }
}