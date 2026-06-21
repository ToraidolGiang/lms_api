package com.example.lms_api.controller;

import com.example.lms_api.dto.request.ReviewRequest;
import com.example.lms_api.dto.request.VoteRequest;
import com.example.lms_api.dto.response.EnrollmentStatusResponse;
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

    // 1. Lấy toàn bộ review của một khóa học (public — phục vụ Tab Reviews)
    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getCourseReviews(@PathVariable Integer courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourseId(courseId));
    }

    // 2. Viết review mới — studentId lấy từ JWT trong Service, không nhận
    // từ client nữa (tránh giả mạo danh tính người review).
    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Integer courseId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.createReview(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 3. Vote review (Upvote / Downvote)
    @PutMapping("/{courseId}/reviews/{reviewId}/vote")
    public ResponseEntity<ReviewResponse> voteReview(
            @PathVariable Integer courseId, // Giữ lại cho đồng nhất URL
            @PathVariable String reviewId,
            @Valid @RequestBody VoteRequest request) {

        ReviewResponse response = reviewService.voteReview(courseId, reviewId, request);
        return ResponseEntity.ok(response);
    }

    // 4. MỚI: kiểm tra trạng thái mua khóa học — GET thuần, không side-effect,
    // không tạo Payment/PayOS như checkout(). Trả enrolled + enrollmentId
    // (cần để gửi kèm khi review).
    @GetMapping("/{courseId}/enrollment-status")
    public ResponseEntity<EnrollmentStatusResponse> getEnrollmentStatus(@PathVariable Integer courseId) {
        return ResponseEntity.ok(reviewService.getEnrollmentStatus(courseId));
    }
}
