// service/impl/LearningServiceImpl.java
package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.learning.CreateDiscussionRequest;
import com.example.lms_api.dto.request.learning.SubmitAssignmentRequest;
import com.example.lms_api.dto.request.learning.SubmitQuizRequest;
import com.example.lms_api.dto.request.learning.SyncVideoRequest;
import com.example.lms_api.dto.response.learning.DiscussionResponse;
import com.example.lms_api.dto.response.learning.ProgressResponse;
import com.example.lms_api.entity.*;
import com.example.lms_api.entity.document.StudentProgress;
import com.example.lms_api.mapper.LearningMapper;
import com.example.lms_api.repository.CourseContentRepository;
import com.example.lms_api.repository.GradebookRepository;
import com.example.lms_api.repository.StudentProgressRepository;
import com.example.lms_api.repository.SubmissionRepository;
import com.example.lms_api.service.LearningService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final StudentProgressRepository progressRepository;
    private final LearningMapper learningMapper;
    // Thêm vào phần đầu của LearningServiceImpl
    private final SubmissionRepository submissionRepository;
    private final CourseContentRepository courseContentRepository; // <-- Thêm repository này
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    // THÊM DÒNG NÀY ĐỂ QUERY TÌM STUDENT:
    private final com.example.lms_api.repository.StudentRepository studentRepository;
    private final GradebookRepository gradebookRepository;
    // Đầu file LearningServiceImpl.java, thêm dòng này dưới các Repository cũ:
    private final com.example.lms_api.repository.DiscussionRepository discussionRepository;

    // Kéo xuống cuối file và thêm 2 hàm này:
    @Override
    public List<DiscussionResponse> getLessonDiscussions(Integer courseId, String lessonId) {
        List<Discussion> discussions = discussionRepository.findByCourseIdAndLessonIdOrderByCreatedAtDesc(courseId, lessonId);

        List<DiscussionResponse> responses = new ArrayList<>();
        for (Discussion d : discussions) {

            // Xử lý lấy tên người đăng bài (móc từ bảng Student/Teacher trong DB của bạn)
            // Tạm thời lấy ID làm tên mặc định, bạn có thể gọi userRepository.findById(d.getAuthorId()) để lấy tên thật
            String authorName = "Học viên #" + d.getAuthorId();

            List<DiscussionResponse.ReplyResponse> replyResponses = new ArrayList<>();
            if (d.getReplies() != null) {
                for (Discussion.Reply r : d.getReplies()) {
                    replyResponses.add(DiscussionResponse.ReplyResponse.builder()
                            .replyId(r.getReplyId())
                            .authorId(r.getAuthorId())
                            .authorName("Người dùng #" + r.getAuthorId()) // Tương tự, ánh xạ tên thật ở đây
                            .authorRole(r.getAuthorRole())
                            .content(r.getContent())
                            .isAccepted(r.isAccepted())
                            .upvotes(r.getUpvotes())
                            .createdAt(r.getCreatedAt())
                            .build());
                }
            }

            responses.add(DiscussionResponse.builder()
                    .id(d.getId())
                    .lessonId(d.getLessonId())
                    .authorId(d.getAuthorId())
                    .authorName(authorName)
                    .authorRole(d.getAuthorRole())
                    .title(d.getTitle())
                    .content(d.getContent())
                    .codeSnippet(d.getCodeSnippet())
                    .upvotes(d.getUpvotes())
                    .replyCount(d.getReplies() != null ? d.getReplies().size() : 0)
                    .createdAt(d.getCreatedAt())
                    .replies(replyResponses)
                    .build());
        }
        return responses;
    }

    @Override
    public DiscussionResponse createDiscussion(Integer courseId, String lessonId, CreateDiscussionRequest request) {
        // Hàm getCurrentStudentId() bạn đã viết sẵn từ trước
        Integer studentId = getCurrentStudentId();

        Discussion discussion = Discussion.builder()
                .courseId(courseId)
                .lessonId(lessonId)
                .authorId(studentId)
                .authorRole("STUDENT")
                .title(request.getTitle())
                .content(request.getContent())
                .codeSnippet(request.getCodeSnippet())
                .tags(request.getTags())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .views(0)
                .upvotes(0)
                .isPinned(false)
                .isSolved(false)
                .build();

        discussionRepository.save(discussion);

        // Cập nhật xong thì gọi lại hàm lấy list để trả về 1 object đầy đủ format DTO
        return getLessonDiscussions(courseId, lessonId).stream()
                .filter(d -> d.getId().equals(discussion.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public ProgressResponse submitQuiz(Integer courseId, String lessonId, SubmitQuizRequest request) {
        Integer studentId = getCurrentStudentId();

        // 1. Tìm cấu trúc bài học trong MongoDB
        CourseContent courseContent = courseContentRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nội dung khóa học"));

        Lesson quizLesson = courseContent.getModules().stream()
                .flatMap(m -> m.getLessons().stream())
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học ID: " + lessonId));

        // 2. Thực hiện logic chấm điểm tự động
        int correctCount = 0;
        int totalQuestions = 0;

        List<Map<String, Object>> questions = (List<Map<String, Object>>) quizLesson.getContent().get("questions");
        if (questions != null) {
            totalQuestions = questions.size();
        }

        Map<String, String> rawStudentAnswers = request.getStudentAnswers();
        Map<String, String> cleanStudentAnswers = new HashMap<>();

        // 🟢 FIX LỖI GSON: Làm sạch dữ liệu từ Client gửi lên (Xóa đuôi ".0" nếu có)
        if (rawStudentAnswers != null) {
            for (Map.Entry<String, String> entry : rawStudentAnswers.entrySet()) {
                String cleanKey = entry.getKey().endsWith(".0") ? entry.getKey().replace(".0", "") : entry.getKey();
                String cleanVal = entry.getValue().endsWith(".0") ? entry.getValue().replace(".0", "") : entry.getValue();
                cleanStudentAnswers.put(cleanKey, cleanVal);
            }
        }

        if (questions != null) {
            for (Map<String, Object> q : questions) {
                // Đọc ID và Đáp án từ DB, cũng khử đuôi ".0" để đồng bộ
                String qIdRaw = String.valueOf(q.get("id"));
                String qId = qIdRaw.endsWith(".0") ? qIdRaw.replace(".0", "") : qIdRaw;

                String correctRaw = String.valueOf(q.get("correctAnswer"));
                String correctAnswer = correctRaw.endsWith(".0") ? correctRaw.replace(".0", "") : correctRaw;

                // Lấy đáp án học viên bằng Key đã làm sạch
                String studentAnswer = cleanStudentAnswers.get(qId);

                // Đối chiếu
                if (studentAnswer != null && studentAnswer.equals(correctAnswer)) {
                    correctCount++;
                }
            }
        }

        // Tính điểm thang 100
        int finalScore = (totalQuestions == 0) ? 0 : (correctCount * 100) / totalQuestions;

        // 🟢 FIX LỖI ÉP KIỂU: Xử lý an toàn biến passingScore từ MongoDB
        Object passScoreObj = quizLesson.getContent().get("passingScore");
        int passingScore = 50; // Mặc định 50 điểm là qua môn
        if (passScoreObj instanceof Number) {
            passingScore = ((Number) passScoreObj).intValue();
        } else if (passScoreObj instanceof String) {
            try { passingScore = Integer.parseInt((String) passScoreObj); } catch (Exception ignored) {}
        }

        boolean isPassed = finalScore >= passingScore;

        String answersJson = "{}";
        try {
            answersJson = objectMapper.writeValueAsString(rawStudentAnswers);
        } catch (Exception e) {
            System.err.println("Lỗi xử lý JSON: " + e.getMessage());
        }

        // 3. Lưu thông tin bài làm vào bảng SQL Submission
        String submissionId = "SUB_" + System.currentTimeMillis();
        Submission submission = Submission.builder()
                .submissionId(submissionId)
                .aqId(lessonId)
                .studentId(studentId)
                .submittedAt(LocalDateTime.now())
                .answers(answersJson)
                .attemptCount(1)
                .build();
        submissionRepository.save(submission);

        // 4. CHẤM ĐIỂM VÀ LƯU VÀO BẢNG SỔ ĐIỂM (GRADEBOOK SQL)
        Gradebook gradebook = Gradebook.builder()
                .gradeId("GRD_" + System.currentTimeMillis())
                .submissionId(submissionId)
                .score((double) finalScore)
                .isPassed(isPassed)
                .feedback(isPassed ? "Hệ thống tự động chấm: Đạt yêu cầu." : "Hệ thống tự động chấm: Chưa đạt yêu cầu.")
                .gradedAt(LocalDateTime.now())
                .gradedBy("SYSTEM_AUTO")
                .build();
        gradebookRepository.save(gradebook);

        // 5. Cập nhật đồng bộ dữ liệu tiến độ vào MongoDB
        StudentProgress progress = getOrCreateProgress(studentId, courseId);
        StudentProgress.LessonProgressData lessonData = getOrCreateLessonProgress(progress, lessonId, 0);

        if (isPassed) {
            lessonData.setStatus("completed");
            lessonData.setCompletedAt(LocalDateTime.now());
            lessonData.setProgressPercent(100.0);

            if (!progress.getProgress().getCompletedLessons().contains(lessonId)) {
                progress.getProgress().getCompletedLessons().add(lessonId);
            }
        } else {
            lessonData.setStatus("in-progress");
        }

        lessonData.setScore(finalScore);
        progress.getProgress().setCurrentLesson(lessonId);
        updateOverallProgress(progress, courseId);
        progressRepository.save(progress);

        return learningMapper.toResponse(progress);
    }

    @Override
    public ProgressResponse getProgress(Integer courseId) {
        Integer studentId = getCurrentStudentId();
        StudentProgress progress = getOrCreateProgress(studentId, courseId);

        // 🟢 ĐỒNG BỘ NGƯỢC: Kiểm tra bảng Gradebook SQL để cập nhật điểm chính xác tuyệt đối lên UI
        if (progress.getLessonProgress() != null) {
            for (StudentProgress.LessonProgressData lp : progress.getLessonProgress()) {
                gradebookRepository.findLatestGradeByStudentAndLesson(studentId, lp.getLessonId())
                        .ifPresent(grade -> {
                            lp.setScore(grade.getScore().intValue());
                            if (grade.getIsPassed()) {
                                lp.setStatus("completed");
                                lp.setProgressPercent(100.0);
                                if (!progress.getProgress().getCompletedLessons().contains(lp.getLessonId())) {
                                    progress.getProgress().getCompletedLessons().add(lp.getLessonId());
                                }
                            }
                        });
            }
        }

        return learningMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public ProgressResponse submitAssignment(Integer courseId, String lessonId, SubmitAssignmentRequest request) {
        Integer studentId = getCurrentStudentId();

        // 1. Lưu link Drive/Cloudinary vào PostgreSQL
        Submission submission = Submission.builder()
                .submissionId("SUB_" + System.currentTimeMillis())
                .aqId(lessonId)
                .studentId(studentId)
                .submittedAt(LocalDateTime.now())
                .fileUrl(request.getFileUrl())
                .answers(request.getStudentNotes())
                .attemptCount(1)
                .build();
        submissionRepository.save(submission);

        // 2. Cập nhật tiến độ MongoDB
        StudentProgress progress = getOrCreateProgress(studentId, courseId);
        StudentProgress.LessonProgressData lessonData = getOrCreateLessonProgress(progress, lessonId, 0);

        lessonData.setStatus("completed"); // Hoặc có thể để "pending-grade" chờ giáo viên chấm
        lessonData.setSubmissionUrl(request.getFileUrl());
        lessonData.setCompletedAt(LocalDateTime.now());
        lessonData.setProgressPercent(100.0);

        if (!progress.getProgress().getCompletedLessons().contains(lessonId)) {
            progress.getProgress().getCompletedLessons().add(lessonId);
        }

        progress.getProgress().setCurrentLesson(lessonId);
        updateOverallProgress(progress, courseId);
        progressRepository.save(progress);

        return learningMapper.toResponse(progress);
    }

    // Hàm phụ trợ để tái sử dụng
    private StudentProgress.LessonProgressData getOrCreateLessonProgress(StudentProgress progress, String lessonId, Integer totalDuration) {
        return progress.getLessonProgress().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst()
                .orElseGet(() -> {
                    StudentProgress.LessonProgressData newData = createNewLessonProgress(lessonId, totalDuration);
                    progress.getLessonProgress().add(newData);
                    return newData;
                });
    }

    private Integer getCurrentStudentId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthenticated");
        }

        // 1. Lấy User ID từ Token JWT
        Integer userId = Integer.parseInt(auth.getName());

        // 2. Chọc vào DB lấy ra Student ID thực sự
        return studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ học viên cho user này!"))
                .getStudentId();
    }

    public void syncVideoProgress(Integer courseId, String lessonId, SyncVideoRequest request) {
        StudentProgress progress = getOrCreateProgress(getCurrentStudentId(), courseId);

        StudentProgress.LessonProgressData lessonData = progress.getLessonProgress().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst()
                .orElseGet(() -> createNewLessonProgress(lessonId, request.getTotalSeconds()));

        // Cập nhật thời gian xem
        lessonData.setWatchedDuration(request.getCurrentSeconds());
        lessonData.setProgressPercent((request.getCurrentSeconds() * 100.0) / request.getTotalSeconds());
        lessonData.setLastAccessedAt(LocalDateTime.now());

        // Logic Auto-Complete: Xem > 90% thì tính là hoàn thành
        if (lessonData.getProgressPercent() >= 90.0 && !"completed".equals(lessonData.getStatus())) {
            lessonData.setStatus("completed");
            lessonData.setCompletedAt(LocalDateTime.now());

            if (!progress.getProgress().getCompletedLessons().contains(lessonId)) {
                progress.getProgress().getCompletedLessons().add(lessonId);
            }
        }

        if (!progress.getLessonProgress().contains(lessonData)) {
            progress.getLessonProgress().add(lessonData);
        }

        progress.getProgress().setCurrentLesson(lessonId);
        progress.setUpdatedAt(LocalDateTime.now());

        // TODO: Cập nhật overallProgress dựa trên tổng số bài học của khóa học
        progressRepository.save(progress);
    }

    private StudentProgress getOrCreateProgress(Integer studentId, Integer courseId) {
        return progressRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseGet(() -> {
                    StudentProgress newProgress = StudentProgress.builder()
                            .studentId(studentId)
                            .courseId(courseId)
                            .progress(StudentProgress.CourseProgressData.builder()
                                    .completedLessons(new ArrayList<>())
                                    .overallProgress(0.0)
                                    .totalWatchTime(0)
                                    .build())
                            .lessonProgress(new ArrayList<>())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return progressRepository.save(newProgress);
                });
    }

    private StudentProgress.LessonProgressData createNewLessonProgress(String lessonId, Integer totalDuration) {
        return StudentProgress.LessonProgressData.builder()
                .lessonId(lessonId)
                .status("in-progress")
                .watchedDuration(0)
                .totalDuration(totalDuration)
                .progressPercent(0.0)
                .build();
    }

    private void updateOverallProgress(StudentProgress progress, Integer courseId) {
        CourseContent courseContent = courseContentRepository.findByCourseId(courseId).orElse(null);

        // Dùng luôn totalLessons từ Metadata mà bạn đã thiết kế sẵn, bỏ qua vòng lặp For!
        if (courseContent != null && courseContent.getMetadata() != null) {
            int totalLessons = courseContent.getMetadata().getTotalLessons();

            if (totalLessons > 0) {
                double percent = (progress.getProgress().getCompletedLessons().size() * 100.0) / totalLessons;
                progress.getProgress().setOverallProgress(Math.round(percent * 100.0) / 100.0);
            }
        }
    }
}