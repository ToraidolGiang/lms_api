package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.ReviewRequest;
import com.example.lms_api.dto.request.VoteRequest;
import com.example.lms_api.dto.response.EnrollmentStatusResponse;
import com.example.lms_api.dto.response.ReviewResponse;
import com.example.lms_api.entity.CourseReview;
import com.example.lms_api.entity.Enrollment;
import com.example.lms_api.entity.Student;
import com.example.lms_api.repository.CourseReviewRepository;
import com.example.lms_api.repository.EnrollmentRepository;
import com.example.lms_api.repository.StudentRepository;
import com.example.lms_api.service.CourseReviewService;
import com.example.lms_api.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {

    private static final String ACTIVE = "Active";

    private final CourseReviewRepository courseReviewRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SecurityUtil securityUtil;

    // ── 1. Lấy danh sách review của khóa học (public, không cần JWT) ──

    @Override
    public List<ReviewResponse> getReviewsByCourseId(Integer courseId) {
        return courseReviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── 2. Tạo review mới — bắt buộc đã enrollment Active ──

    @Override
    @Transactional
    public ReviewResponse createReview(Integer courseId, ReviewRequest request) {
        Student student = securityUtil.getCurrentStudent();

        Enrollment enrollment = enrollmentRepository
                .findFirstByCourse_CourseIdAndStudent_StudentIdAndAccessStatusOrderByEnrollDateDesc(
                        courseId, student.getStudentId(), ACTIVE)
                .orElseThrow(() -> new RuntimeException(
                        "Bạn cần mua khóa học này trước khi viết đánh giá."));

        CourseReview.ReviewDetails details = CourseReview.ReviewDetails.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .pros(request.getPros() != null ? request.getPros() : new ArrayList<>())
                .cons(request.getCons() != null ? request.getCons() : new ArrayList<>())
                .build();

        CourseReview.HelpfulStats helpful = CourseReview.HelpfulStats.builder()
                .upvotes(0)
                .downvotes(0)
                .votedBy(new ArrayList<>())
                .build();

        CourseReview entity = CourseReview.builder()
                .courseId(courseId)
                .studentId(student.getStudentId())
                .enrollmentId(enrollment.getEnrollmentId())
                .rating(request.getRating())
                .review(details)
                .helpful(helpful)
                .isVerified(true) // đã xác minh có enrollment Active
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CourseReview saved = courseReviewRepository.save(entity);
        return toResponse(saved, student);
    }

    // ── 3. Vote review (giữ nguyên hành vi cũ, chỉ map đúng cấu trúc nested) ──

    @Override
    @Transactional
    public ReviewResponse voteReview(Integer courseId, String reviewId, VoteRequest request) {
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại: " + reviewId));

        if (review.getCourseId() == null || !review.getCourseId().equals(courseId)) {
            throw new RuntimeException("Bài đánh giá không thuộc khóa học này!");
        }

        // Lấy studentId của tài khoản đang đăng nhập từ JWT để bảo mật
        Student student = securityUtil.getCurrentStudent();
        Integer studentId = student.getStudentId();
        
        CourseReview.HelpfulStats helpful = review.getHelpful();
        if (helpful == null) {
            helpful = CourseReview.HelpfulStats.builder()
                    .upvotes(0).downvotes(0).votedBy(new ArrayList<>()).votedUpBy(new ArrayList<>()).votedDownBy(new ArrayList<>()).build();
            review.setHelpful(helpful);
        }
        if (helpful.getVotedBy() == null) {
            helpful.setVotedBy(new ArrayList<>());
        }
        if (helpful.getVotedUpBy() == null) {
            helpful.setVotedUpBy(new ArrayList<>());
        }
        if (helpful.getVotedDownBy() == null) {
            helpful.setVotedDownBy(new ArrayList<>());
        }

        boolean alreadyVotedUp = helpful.getVotedUpBy().contains(studentId);
        boolean alreadyVotedDown = helpful.getVotedDownBy().contains(studentId);
        boolean wantsUpvote = Boolean.TRUE.equals(request.getIsUpvote());

        if (wantsUpvote) {
            if (alreadyVotedUp) {
                // Hủy upvote
                helpful.getVotedUpBy().remove(studentId);
                helpful.getVotedBy().remove(studentId);
                helpful.setUpvotes(Math.max(0, (helpful.getUpvotes() == null ? 0 : helpful.getUpvotes()) - 1));
            } else {
                // Nếu đang downvote thì phải hủy downvote trước
                if (alreadyVotedDown) {
                    helpful.getVotedDownBy().remove(studentId);
                    helpful.setDownvotes(Math.max(0, (helpful.getDownvotes() == null ? 0 : helpful.getDownvotes()) - 1));
                } else {
                    // Chưa vote gì cả -> Thêm vào votedBy chung
                    helpful.getVotedBy().add(studentId);
                }
                // Thực hiện upvote
                helpful.getVotedUpBy().add(studentId);
                helpful.setUpvotes((helpful.getUpvotes() == null ? 0 : helpful.getUpvotes()) + 1);
            }
        } else {
            if (alreadyVotedDown) {
                // Hủy downvote
                helpful.getVotedDownBy().remove(studentId);
                helpful.getVotedBy().remove(studentId);
                helpful.setDownvotes(Math.max(0, (helpful.getDownvotes() == null ? 0 : helpful.getDownvotes()) - 1));
            } else {
                // Nếu đang upvote thì phải hủy upvote trước
                if (alreadyVotedUp) {
                    helpful.getVotedUpBy().remove(studentId);
                    helpful.setUpvotes(Math.max(0, (helpful.getUpvotes() == null ? 0 : helpful.getUpvotes()) - 1));
                } else {
                    // Chưa vote gì cả -> Thêm vào votedBy chung
                    helpful.getVotedBy().add(studentId);
                }
                // Thực hiện downvote
                helpful.getVotedDownBy().add(studentId);
                helpful.setDownvotes((helpful.getDownvotes() == null ? 0 : helpful.getDownvotes()) + 1);
            }
        }

        review.setUpdatedAt(LocalDateTime.now());
        CourseReview saved = courseReviewRepository.save(review);
        return toResponse(saved);
    }

    // ── 4. Enrollment status (GET thuần, không side-effect) ──

    @Override
    public EnrollmentStatusResponse getEnrollmentStatus(Integer courseId) {
        Student student = securityUtil.getCurrentStudent();

        return enrollmentRepository
                .findFirstByCourse_CourseIdAndStudent_StudentIdAndAccessStatusOrderByEnrollDateDesc(
                        courseId, student.getStudentId(), ACTIVE)
                .map(e -> EnrollmentStatusResponse.builder()
                        .enrolled(true)
                        .enrollmentId(e.getEnrollmentId())
                        .build())
                .orElse(EnrollmentStatusResponse.builder()
                        .enrolled(false)
                        .enrollmentId(null)
                        .build());
    }

    // ── Mapping: CourseReview (entity, nested) → ReviewResponse (DTO, phẳng) ──

    private ReviewResponse toResponse(CourseReview entity) {
        Student student = studentRepository.findByStudentId(entity.getStudentId()).orElse(null);
        return toResponse(entity, student);
    }

    private ReviewResponse toResponse(CourseReview entity, Student student) {
        CourseReview.ReviewDetails details = entity.getReview();
        CourseReview.HelpfulStats helpful = entity.getHelpful();

        String studentName = student != null
                ? (student.getFirstName() + " " + student.getLastName()).trim()
                : "Học viên";

        return ReviewResponse.builder()
                .id(entity.getId())
                .courseId(entity.getCourseId())
                .studentId(entity.getStudentId())
                .studentName(studentName)
                .rating(entity.getRating())
                .title(details != null ? details.getTitle() : null)
                .content(details != null ? details.getContent() : null)
                .pros(details != null ? details.getPros() : Collections.emptyList())
                .cons(details != null ? details.getCons() : Collections.emptyList())
                .upvotes(helpful != null ? helpful.getUpvotes() : 0)
                .downvotes(helpful != null ? helpful.getDownvotes() : 0)
                .votedBy(helpful != null ? helpful.getVotedBy() : Collections.emptyList())
                .votedUpBy(helpful != null ? helpful.getVotedUpBy() : Collections.emptyList())
                .votedDownBy(helpful != null ? helpful.getVotedDownBy() : Collections.emptyList())
                .isVerified(entity.getIsVerified())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}