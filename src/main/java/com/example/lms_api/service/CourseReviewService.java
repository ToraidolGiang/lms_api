package com.example.lms_api.service;

import com.example.lms_api.dto.request.ReviewRequest;
import com.example.lms_api.dto.request.VoteRequest;
import com.example.lms_api.dto.response.ReviewResponse;
import java.util.List;

public interface CourseReviewService {
    List<ReviewResponse> getReviewsByCourseId(Integer courseId);
    ReviewResponse createReview(Integer courseId, Integer studentId, ReviewRequest request);
    ReviewResponse voteReview(String reviewId, VoteRequest request);
}