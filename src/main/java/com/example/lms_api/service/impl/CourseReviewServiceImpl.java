package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.VoteRequest;
import com.example.lms_api.entity.CourseReview;
import com.example.lms_api.dto.request.ReviewRequest;
import com.example.lms_api.dto.response.ReviewResponse;
import com.example.lms_api.repository.CourseReviewRepository;
import com.example.lms_api.service.CourseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseReviewRepository reviewRepository;
    // Inject thêm StudentRepository (PostgreSQL) nếu bạn cần lấy FullName sinh viên
    // private final StudentRepository studentRepository;

    @Override
    public List<ReviewResponse> getReviewsByCourseId(Integer courseId) {
        return reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse createReview(Integer courseId, Integer studentId, ReviewRequest request) {
        CourseReview courseReview = CourseReview.builder()
                .courseId(courseId)
                .studentId(studentId)
                .enrollmentId(request.getEnrollmentId())
                .rating(request.getRating())
                .review(CourseReview.ReviewDetails.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .pros(request.getPros())
                        .cons(request.getCons())
                        .build())
                .helpful(CourseReview.HelpfulStats.builder()
                        .upvotes(0)
                        .downvotes(0)
                        .votedBy(List.of())
                        .build())
                .isVerified(true) // Tự động xác thực hoặc xử lý logic nghiệp vụ riêng
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CourseReview savedReview = reviewRepository.save(courseReview);
        return mapToResponse(savedReview);
    }

    private ReviewResponse mapToResponse(CourseReview doc) {
        // Tạm thời fake hoặc query tìm tên Student theo doc.getStudentId() tại PostgreSQL
        String studentName = "Học viên ẩn danh (ID: " + doc.getStudentId() + ")";

        return ReviewResponse.builder()
                .id(doc.getId())
                .courseId(doc.getCourseId())
                .studentId(doc.getStudentId())
                .studentName(studentName)
                .rating(doc.getRating())
                .title(doc.getReview().getTitle())
                .content(doc.getReview().getContent())
                .pros(doc.getReview().getPros())
                .cons(doc.getReview().getCons())
                .upvotes(doc.getHelpful() != null ? doc.getHelpful().getUpvotes() : 0)
                .isVerified(doc.getIsVerified())
                .createdAt(doc.getCreatedAt())
                .build();
    }

    @Override
    public ReviewResponse voteReview(String reviewId, VoteRequest request) {
        // 1. Tìm review trong MongoDB
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đánh giá với ID: " + reviewId));

        // Khởi tạo block helpful nếu data cũ bị null
        if (review.getHelpful() == null) {
            review.setHelpful(CourseReview.HelpfulStats.builder()
                    .upvotes(0).downvotes(0).votedBy(new java.util.ArrayList<>()).build());
        }

        // 2. Kiểm tra xem học viên này đã vote chưa (Chống spam)
        if (review.getHelpful().getVotedBy().contains(request.getStudentId())) {
            throw new RuntimeException("Bạn đã vote cho bài đánh giá này rồi!");
        }

        // 3. Tăng số lượng vote
        if (request.getIsUpvote()) {
            review.getHelpful().setUpvotes(review.getHelpful().getUpvotes() + 1);
        } else {
            review.getHelpful().setDownvotes(review.getHelpful().getDownvotes() + 1);
        }

        // 4. Lưu studentId vào danh sách đã vote
        review.getHelpful().getVotedBy().add(request.getStudentId());
        review.setUpdatedAt(java.time.LocalDateTime.now());

        // 5. Lưu lại vào MongoDB và trả về kết quả
        CourseReview savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }
}