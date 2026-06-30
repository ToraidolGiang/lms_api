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
import com.example.lms_api.repository.*;
import com.example.lms_api.service.LearningService;
import com.example.lms_api.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final CourseGradeRepository courseGradeRepository;
    // Đầu file LearningServiceImpl.java, thêm dòng này dưới các Repository cũ:
    private final com.example.lms_api.repository.DiscussionRepository discussionRepository;
    private final SecurityUtil securityUtil;

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
        Integer studentId = securityUtil.getCurrentStudentId();

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
        Integer studentId = securityUtil.getCurrentStudentId();

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

        // Lấy lịch sử làm bài để tính số lần thử
        java.util.List<Submission> previousSubmissions = submissionRepository.findByStudentIdAndAqId(studentId, lessonId);
        int currentAttempt = previousSubmissions.size() + 1;

        if (currentAttempt > 5) {
            throw new RuntimeException("Bạn đã vượt quá số lần làm bài tối đa (5 lần).");
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
                .type("quiz")
                .attemptCount(currentAttempt)
                .build();
        submissionRepository.save(submission);

        // 4. CHẤM ĐIỂM VÀ LƯU VÀO BẢNG SỔ ĐIỂM (GRADEBOOK SQL)
        java.util.Optional<Gradebook> existingGradebookOpt = gradebookRepository.findLatestGradeByStudentAndLesson(studentId, lessonId);
        boolean isHigherScore = true; // Mặc định là điểm cao hơn nếu chưa làm bao giờ

        if (existingGradebookOpt.isPresent()) {
            Gradebook existingGradebook = existingGradebookOpt.get();
            if (finalScore > existingGradebook.getScore()) {
                existingGradebook.setScore((double) finalScore);
                existingGradebook.setIsPassed(isPassed);
                existingGradebook.setFeedback(isPassed ? "Hệ thống tự động chấm: Đạt yêu cầu." : "Hệ thống tự động chấm: Chưa đạt yêu cầu.");
                existingGradebook.setSubmissionId(submissionId);
                existingGradebook.setGradedAt(LocalDateTime.now());
                gradebookRepository.save(existingGradebook);
            } else {
                isHigherScore = false; // Lần này điểm thấp hơn hoặc bằng, không update Sổ điểm
            }
        } else {
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
        }

        // 5. Cập nhật đồng bộ dữ liệu tiến độ vào MongoDB
        StudentProgress progress = getOrCreateProgress(studentId, courseId);
        StudentProgress.LessonProgressData lessonData = getOrCreateLessonProgress(progress, lessonId, 0);

        lessonData.setAttemptCount(currentAttempt);
        lessonData.setMaxAttempts(5);

        if (isHigherScore) {
            lessonData.setScore(finalScore);
            if (isPassed) {
                lessonData.setStatus("completed");
                lessonData.setCompletedAt(LocalDateTime.now());
                lessonData.setProgressPercent(100.0);

                if (!progress.getProgress().getCompletedLessons().contains(lessonId)) {
                    progress.getProgress().getCompletedLessons().add(lessonId);
                }
            } else {
                // Chỉ set in-progress nếu điểm mới cao hơn nhưng vẫn tạch (và chưa pass bao giờ)
                if (!"completed".equals(lessonData.getStatus())) {
                    lessonData.setStatus("in-progress");
                }
            }
        }

        progress.getProgress().setCurrentLesson(lessonId);
        updateOverallProgress(progress, courseId);
        progressRepository.save(progress);

        return learningMapper.toResponse(progress);
    }

    @Override
    public ProgressResponse getProgress(Integer courseId) {
        Integer studentId = securityUtil.getCurrentStudentId();
        StudentProgress progress = getOrCreateProgress(studentId, courseId);
        boolean hasChanges = false;

        // 🟢 ĐỒNG BỘ NGƯỢC: Kiểm tra bảng Gradebook SQL để cập nhật điểm chính xác tuyệt đối lên UI
        if (progress.getLessonProgress() != null) {
            for (StudentProgress.LessonProgressData lp : progress.getLessonProgress()) {
                var gradeOpt = gradebookRepository.findLatestGradeByStudentAndLesson(studentId, lp.getLessonId());
                if (gradeOpt.isPresent()) {
                    var grade = gradeOpt.get();
                    Integer newScore = grade.getScore().intValue();
                    if (!newScore.equals(lp.getScore())) {
                        lp.setScore(newScore);
                        hasChanges = true;
                    }
                    if (grade.getIsPassed() && !"completed".equals(lp.getStatus())) {
                        lp.setStatus("completed");
                        lp.setProgressPercent(100.0);
                        if (!progress.getProgress().getCompletedLessons().contains(lp.getLessonId())) {
                            progress.getProgress().getCompletedLessons().add(lp.getLessonId());
                        }
                        hasChanges = true;
                    }
                }
            }
        }

        if (hasChanges) {
            updateOverallProgress(progress, courseId);
            progressRepository.save(progress);
        }

        ProgressResponse response = learningMapper.toResponse(progress);

        // Bổ sung dữ liệu attemptCount và maxAttempts cho quiz/assignment
        CourseContent courseContent = courseContentRepository.findByCourseId(courseId).orElse(null);
        Map<String, String> lessonTypeMap = new HashMap<>();
        if (courseContent != null && courseContent.getModules() != null) {
            courseContent.getModules().forEach(m -> {
                if (m.getLessons() != null) {
                    m.getLessons().forEach(l -> lessonTypeMap.put(l.getLessonId(), l.getType()));
                }
            });
        }

        if (response != null) {
            java.util.List<ProgressResponse.LessonDetailProgress> details = response.getLessonDetails();
            if (details == null) {
                details = new ArrayList<>();
                response.setLessonDetails(details);
            }

            // Đảm bảo tất cả quiz/assignment đều có mặt trong danh sách, kể cả chưa từng mở ra
            for (Map.Entry<String, String> entry : lessonTypeMap.entrySet()) {
                String lId = entry.getKey();
                String lType = entry.getValue();

                if ("quiz".equalsIgnoreCase(lType) || "assignment".equalsIgnoreCase(lType)) {
                    ProgressResponse.LessonDetailProgress existingDetail = details.stream()
                            .filter(d -> d.getLessonId().equals(lId))
                            .findFirst()
                            .orElse(null);

                    if (existingDetail == null) {
                        existingDetail = ProgressResponse.LessonDetailProgress.builder()
                                .lessonId(lId)
                                .status("not-started")
                                .progressPercent(0.0)
                                .build();
                        details.add(existingDetail);
                    }

                    java.util.List<Submission> subs = submissionRepository.findByStudentIdAndAqId(studentId, lId);
                    existingDetail.setAttemptCount(subs != null ? subs.size() : 0);
                    existingDetail.setMaxAttempts("quiz".equalsIgnoreCase(lType) ? 5 : 1);
                }
            }
        }

        return response;
    }

    @Override
    @Transactional
    public ProgressResponse submitAssignment(Integer courseId, String lessonId, SubmitAssignmentRequest request) {
        Integer studentId = securityUtil.getCurrentStudentId();

        // Lấy lịch sử nộp bài Assignment
        java.util.List<Submission> previousSubmissions = submissionRepository.findByStudentIdAndAqId(studentId, lessonId);
        if (!previousSubmissions.isEmpty()) {
            throw new RuntimeException("Đây là bài thi cuối khóa. Bạn chỉ được phép nộp 1 lần duy nhất!");
        }

        // 1. Lưu link Drive/Cloudinary vào PostgreSQL
        Submission submission = Submission.builder()
                .submissionId("SUB_" + System.currentTimeMillis())
                .aqId(lessonId)
                .studentId(studentId)
                .submittedAt(LocalDateTime.now())
                .fileUrl(request.getFileUrl())
                .answers(request.getStudentNotes())
                .attemptCount(1)
                .type("assignment")
                .build();
        submissionRepository.save(submission);

        CourseGrade courseGrade = CourseGrade.builder()
                .courseId(courseId)
                .studentId(studentId)
                .isMasked(false)
                .build();
        courseGradeRepository.save(courseGrade);

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


    public void syncVideoProgress(Integer courseId, String lessonId, SyncVideoRequest request) {
        if (request == null || request.getTotalSeconds() == null || request.getTotalSeconds() <= 0) {
            throw new IllegalArgumentException("Tổng số giây video không hợp lệ!");
        }

        StudentProgress progress = getOrCreateProgress(securityUtil.getCurrentStudentId(), courseId);
        StudentProgress.LessonProgressData lessonData = getOrCreateLessonProgress(progress, lessonId, request.getTotalSeconds());

        // Đảm bảo totalDuration được cập nhật nếu trước đó chưa có hoặc khác biệt
        if (lessonData.getTotalDuration() == null || lessonData.getTotalDuration() <= 0) {
            lessonData.setTotalDuration(request.getTotalSeconds());
        }

        int currentSec = request.getCurrentSeconds() != null ? request.getCurrentSeconds() : 0;
        double percent = (currentSec * 100.0) / request.getTotalSeconds();
        if (percent > 100.0) percent = 100.0;
        if (percent < 0.0) percent = 0.0;

        // Cập nhật thời gian xem
        lessonData.setWatchedDuration(currentSec);
        lessonData.setProgressPercent(percent);
        lessonData.setLastAccessedAt(LocalDateTime.now());

        // Logic Auto-Complete: Xem >= 90% thì tính là hoàn thành
        if (percent >= 90.0 && !"completed".equals(lessonData.getStatus())) {
            lessonData.setStatus("completed");
            lessonData.setCompletedAt(LocalDateTime.now());

            if (!progress.getProgress().getCompletedLessons().contains(lessonId)) {
                progress.getProgress().getCompletedLessons().add(lessonId);
            }
        }

        progress.getProgress().setCurrentLesson(lessonId);
        progress.setUpdatedAt(LocalDateTime.now());

        // Cập nhật overallProgress dựa trên tổng số bài học của khóa học trước khi lưu
        updateOverallProgress(progress, courseId);
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
        if (courseContent == null) return;

        int totalLessons = 0;
        if (courseContent.getModules() != null) {
            // Luôn ưu tiên đếm trực tiếp từ danh sách các bài học trong Modules để chính xác tuyệt đối
            totalLessons = courseContent.getModules().stream()
                    .filter(m -> m.getLessons() != null)
                    .mapToInt(m -> m.getLessons().size())
                    .sum();
        } else if (courseContent.getMetadata() != null && courseContent.getMetadata().getTotalLessons() > 0) {
            // Fallback: Lấy từ metadata nếu modules rỗng
            totalLessons = courseContent.getMetadata().getTotalLessons();
        }

        if (totalLessons > 0) {
            double percent = (progress.getProgress().getCompletedLessons().size() * 100.0) / totalLessons;
            if (percent > 100.0) percent = 100.0;
            progress.getProgress().setOverallProgress(Math.round(percent * 100.0) / 100.0);
        }
    }
}