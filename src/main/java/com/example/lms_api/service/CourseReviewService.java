package com.example.lms_api.service;

import com.example.lms_api.dto.request.ReviewRequest;
import com.example.lms_api.dto.request.VoteRequest;
import com.example.lms_api.dto.response.EnrollmentStatusResponse;
import com.example.lms_api.dto.response.ReviewResponse;

import java.util.List;

public interface CourseReviewService {

    List<ReviewResponse> getReviewsByCourseId(Integer courseId);

    /**
     * studentId KHÔNG còn là tham số — Impl tự lấy từ SecurityContext (JWT)
     * để tránh giả mạo danh tính người review.
     */
    ReviewResponse createReview(Integer courseId, ReviewRequest request);

    ReviewResponse voteReview(Integer courseId, String reviewId, VoteRequest request);

    /**
     * GET thuần, không side-effect — trả enrolled=true/false và enrollmentId
     * (nếu có) của student hiện tại (JWT) cho courseId này.
     */
    EnrollmentStatusResponse getEnrollmentStatus(Integer courseId);
}
