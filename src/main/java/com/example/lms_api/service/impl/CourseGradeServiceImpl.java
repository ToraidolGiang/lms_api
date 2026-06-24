package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CourseGradeRequest;
import com.example.lms_api.dto.request.SubmitGradeRequest;
import com.example.lms_api.dto.response.CourseGradeResponse;
import com.example.lms_api.dto.response.GradingListResponse;
import com.example.lms_api.entity.CourseGrade;
import com.example.lms_api.entity.Gradebook;
import com.example.lms_api.entity.Student;
import com.example.lms_api.entity.Submission;
import com.example.lms_api.mapper.CourseGradeMapper;
import com.example.lms_api.repository.*;
import com.example.lms_api.service.CourseGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseGradeServiceImpl implements CourseGradeService {

    private final CourseGradeRepository courseGradeRepository;
    private final CourseGradeMapper     courseGradeMapper;
    private final SubmissionRepository  submissionRepository;
    private final GradebookRepository   gradebookRepository;
    private final StudentRepository     studentRepository;

    // ────────────────────────────────────────────────────────────
    // CRUD cũ
    // ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CourseGradeResponse createGrade(CourseGradeRequest request) {
        CourseGrade grade = courseGradeMapper.toEntity(request);
        grade.setGradedAt(LocalDateTime.now());
        if (grade.getIsMasked() == null) grade.setIsMasked(false);
        return courseGradeMapper.toResponse(courseGradeRepository.save(grade));
    }

    @Override
    @Transactional
    public CourseGradeResponse updateGrade(Integer id, CourseGradeRequest request) {
        CourseGrade grade = findOrThrow(id);
        courseGradeMapper.updateEntityFromRequest(request, grade);
        return courseGradeMapper.toResponse(courseGradeRepository.save(grade));
    }

    @Override
    @Transactional
    public CourseGradeResponse toggleMask(Integer id, Boolean masked) {
        CourseGrade grade = findOrThrow(id);
        grade.setIsMasked(masked);
        return courseGradeMapper.toResponse(courseGradeRepository.save(grade));
    }

    @Override
    public List<CourseGradeResponse> getGradesByCourse(Integer courseId) {
        return courseGradeRepository.findByCourseId(courseId)
                .stream()
                .map(courseGradeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseGradeResponse> getGradesByStudentForStudent(Integer studentId) {
        return courseGradeRepository.findByStudentId(studentId)
                .stream()
                .map(courseGradeMapper::toResponseForStudent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void submitOrUpdateGrade(CourseGradeRequest request) {
        Optional<CourseGrade> existing = courseGradeRepository
                .findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());
        if (existing.isPresent()) {
            updateGrade(existing.get().getCourseGradeId(), request);
        } else {
            createGrade(request);
        }
    }

    // ────────────────────────────────────────────────────────────
    // GRADING LIST — danh sách + thống kê cho giáo viên
    // ────────────────────────────────────────────────────────────

    @Override
    public GradingListResponse getGradingList(Integer courseId) {
        List<CourseGrade> grades = courseGradeRepository.findByCourseId(courseId);

        Map<Integer, CourseGrade> gradeMap = grades.stream()
                .collect(Collectors.toMap(CourseGrade::getStudentId, g -> g, (a, b) -> a));

        List<Integer> studentIds = new ArrayList<>(gradeMap.keySet());

        // Lấy submission mới nhất (assignment) của từng sinh viên
        Map<Integer, Submission> latestSubMap = new HashMap<>();
        for (Integer sid : studentIds) {
            submissionRepository
                    .findTopByStudentIdAndTypeOrderBySubmittedAtDesc(sid, "assignment")
                    .ifPresent(s -> latestSubMap.put(sid, s));
        }

        List<GradingListResponse.StudentGradingItem> items = new ArrayList<>();
        for (Integer sid : studentIds) {
            CourseGrade grade   = gradeMap.get(sid);
            Submission  sub     = latestSubMap.get(sid);
            Student     student = studentRepository.findById(sid).orElse(null);

            String fullName = student != null
                    ? (student.getFirstName() + " " + student.getLastName()).trim()
                    : "Sinh viên #" + sid;

            items.add(GradingListResponse.StudentGradingItem.builder()
                    .studentId(sid)
                    .fullName(fullName)
                    .graded(grade.getExamScore() != null)
                    .isMasked(grade.getIsMasked())
                    .courseGradeId(grade.getCourseGradeId())
                    .examScore(grade.getExamScore())
                    .quizAvgScore(grade.getQuizAvgScore())
                    .finalScore(grade.getFinalScore())
                    .gradeLevel(grade.getGradeLevel())
                    .gradedAt(grade.getGradedAt())
                    .submissionId(sub != null ? sub.getSubmissionId() : null)
                    .fileUrl(sub != null ? sub.getFileUrl() : null)
                    .submittedAt(sub != null ? sub.getSubmittedAt() : null)
                    .build());
        }

        // Chưa chấm lên đầu
        items.sort(Comparator.comparing(GradingListResponse.StudentGradingItem::isGraded));

        long gradedCount = items.stream().filter(GradingListResponse.StudentGradingItem::isGraded).count();
        long maskedCount = items.stream().filter(i -> Boolean.TRUE.equals(i.getIsMasked())).count();
        OptionalDouble avg = items.stream()
                .filter(i -> i.getFinalScore() != null)
                .mapToDouble(i -> i.getFinalScore().doubleValue())
                .average();

        GradingListResponse.GradingStats stats = GradingListResponse.GradingStats.builder()
                .totalStudents(items.size())
                .gradedCount((int) gradedCount)
                .ungradedCount(items.size() - (int) gradedCount)
                .maskedCount((int) maskedCount)
                .avgFinalScore(avg.isPresent() ? Math.round(avg.getAsDouble() * 100.0) / 100.0 : null)
                .build();

        return GradingListResponse.builder().stats(stats).students(items).build();
    }

    // ────────────────────────────────────────────────────────────
    // SUBMIT GRADE — giáo viên nhập examScore → tính + ghi CourseGrade
    // ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public GradingListResponse.StudentGradingItem submitGrade(SubmitGradeRequest request) {
        Integer studentId = request.getStudentId();
        Integer courseId  = request.getCourseId();

        // 1. Tính quizAvgScore từ tất cả quiz submission của sinh viên
        BigDecimal quizAvg = calcQuizAvg(studentId);

        // 2. finalScore = (examScore + quizAvgScore) / 2
        BigDecimal examScore  = request.getExamScore();
        BigDecimal finalScore = examScore.add(quizAvg)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        // 3. Xếp loại
        String gradeLevel = calcGradeLevel(finalScore);

        // 4. Upsert vào CourseGrade
        CourseGrade grade = courseGradeRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElse(CourseGrade.builder()
                        .studentId(studentId)
                        .courseId(courseId)
                        .isMasked(false)
                        .build());

        grade.setExamScore(examScore);
        grade.setQuizAvgScore(quizAvg);
        grade.setFinalScore(finalScore);
        grade.setGradeLevel(gradeLevel);
        grade.setGradedAt(LocalDateTime.now());
        if (request.getIsMasked() != null) grade.setIsMasked(request.getIsMasked());

        courseGradeRepository.save(grade);

        // 5. Build response
        Submission latestSub = submissionRepository
                .findTopByStudentIdAndTypeOrderBySubmittedAtDesc(studentId, "assignment")
                .orElse(null);

        Student student = studentRepository.findById(studentId).orElse(null);
        String fullName = student != null
                ? (student.getFirstName() + " " + student.getLastName()).trim()
                : "Sinh viên #" + studentId;

        return GradingListResponse.StudentGradingItem.builder()
                .studentId(studentId)
                .fullName(fullName)
                .graded(true)
                .isMasked(grade.getIsMasked())
                .courseGradeId(grade.getCourseGradeId())
                .examScore(examScore)
                .quizAvgScore(quizAvg)
                .finalScore(finalScore)
                .gradeLevel(gradeLevel)
                .gradedAt(grade.getGradedAt())
                .submissionId(latestSub != null ? latestSub.getSubmissionId() : null)
                .fileUrl(latestSub != null ? latestSub.getFileUrl() : null)
                .submittedAt(latestSub != null ? latestSub.getSubmittedAt() : null)
                .build();
    }

    // ────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────

    private BigDecimal calcQuizAvg(Integer studentId) {
        List<Submission> quizSubs = submissionRepository
                .findByStudentIdAndType(studentId, "quiz");
        if (quizSubs.isEmpty()) return BigDecimal.ZERO;

        double sum = 0; int count = 0;
        for (Submission sub : quizSubs) {
            Optional<Gradebook> gb = gradebookRepository
                    .findTopBySubmissionIdOrderByGradedAtDesc(sub.getSubmissionId());
            if (gb.isPresent() && gb.get().getScore() != null) {
                sum += gb.get().getScore();
                count++;
            }
        }
        if (count == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(sum / count).setScale(2, RoundingMode.HALF_UP);
    }

    private String calcGradeLevel(BigDecimal score) {
        double s = score.doubleValue();
        if (s >= 85) return "A";
        if (s >= 80) return "B+";
        if (s >= 70) return "B";
        if (s >= 65) return "C+";
        if (s >= 55) return "C";
        if (s >= 50) return "D+";
        if (s >= 40) return "D";
        return "F";
    }

    private CourseGrade findOrThrow(Integer id) {
        return courseGradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CourseGrade ID: " + id));
    }
}