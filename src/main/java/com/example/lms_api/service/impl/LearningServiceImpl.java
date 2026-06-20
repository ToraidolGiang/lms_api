// service/impl/LearningServiceImpl.java
package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.learning.SubmitAssignmentRequest;
import com.example.lms_api.dto.request.learning.SubmitQuizRequest;
import com.example.lms_api.dto.request.learning.SyncVideoRequest;
import com.example.lms_api.dto.response.learning.ProgressResponse;
import com.example.lms_api.entity.CourseContent;
import com.example.lms_api.entity.Lesson;
import com.example.lms_api.entity.Submission;
import com.example.lms_api.entity.document.StudentProgress;
import com.example.lms_api.mapper.LearningMapper;
import com.example.lms_api.repository.CourseContentRepository;
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

    @Override
    @Transactional
    public ProgressResponse submitQuiz(Integer courseId, String lessonId, SubmitQuizRequest request) {
        Integer studentId = getCurrentStudentId();

        // 1. TÌM BÀI QUIZ TỪ MONGODB (Thay thế hàm findLesson)
        CourseContent courseContent = courseContentRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nội dung khóa học"));

        // Dùng Stream để tìm Lesson trong tất cả các Module
        Lesson quizLesson = courseContent.getModules().stream()
                .flatMap(m -> m.getLessons().stream())
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học ID: " + lessonId));

        // 2. LOGIC CHẤM ĐIỂM
        int correctCount = 0;
        int totalQuestions = 0;

        List<Map<String, Object>> questions = (List<Map<String, Object>>) quizLesson.getContent().get("questions");
        if (questions != null) {
            totalQuestions = questions.size();
        }

        Map<String, String> studentAnswers = request.getStudentAnswers();

        if (questions != null && studentAnswers != null) {
            for (Map<String, Object> q : questions) {
                String qId = String.valueOf(q.get("id"));
                String correctAnswer = String.valueOf(q.get("correctAnswer"));
                String studentAnswer = studentAnswers.get(qId);

                if (studentAnswer != null && studentAnswer.equals(correctAnswer)) {
                    correctCount++;
                }
            }
        }

        int finalScore = (totalQuestions == 0) ? 0 : (correctCount * 100) / totalQuestions;
        Integer passingScore = (Integer) quizLesson.getContent().getOrDefault("passingScore", 50);
        boolean isPassed = finalScore >= passingScore;

        // Xử lý bắt lỗi cho ObjectMapper để hết gạch đỏ
        String answersJson = "{}";
        try {
            answersJson = objectMapper.writeValueAsString(studentAnswers);
        } catch (Exception e) {
            System.err.println("Lỗi parse JSON đáp án: " + e.getMessage());
        }

        // 3. LƯU BÀI LÀM VÀO POSTGRESQL
        Submission submission = Submission.builder()
                .submissionId("SUB_" + System.currentTimeMillis())
                .aqId(lessonId)
                .studentId(studentId)
                .submittedAt(LocalDateTime.now())
                .answers(answersJson) // Đã an toàn sau khi try-catch
                .attemptCount(1)
                .build();
        submissionRepository.save(submission);

        // 4. CẬP NHẬT TIẾN ĐỘ LÊN MONGODB
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
        progressRepository.save(progress);

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

    public ProgressResponse getProgress(Integer courseId) {
        StudentProgress progress = getOrCreateProgress(getCurrentStudentId(), courseId);
        return learningMapper.toResponse(progress);
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

}