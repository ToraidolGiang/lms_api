package com.example.lms_api.service.impl;

import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.TeacherDashboardResponse;
import com.example.lms_api.dto.response.dashboard.*;
import com.example.lms_api.entity.Course;
import com.example.lms_api.entity.Discussion;
import com.example.lms_api.entity.Teacher;
import com.example.lms_api.repository.*;
import com.example.lms_api.service.TeacherDashboardService;
import com.example.lms_api.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherDashboardServiceImpl implements TeacherDashboardService {

    private final CourseRepository      courseRepository;
    private final EnrollmentRepository  enrollmentRepository;
    private final SubmissionRepository  submissionRepository;
    private final GradebookRepository   gradebookRepository;
    private final DiscussionRepository  discussionRepository;
    private final TeacherRepository     teacherRepository;
    private final StudentRepository     studentRepository;

    // ─────────────────────────────────────────────────────────────────────────
    //  (Cũ) getDashboard – giữ nguyên để không break Android đang dùng
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public TeacherDashboardResponse getDashboard(Integer userId) {
        long totalStudents       = enrollmentRepository.countDistinctStudentsByTeacherId(userId);
        long enrollmentsThisMonth= enrollmentRepository.countEnrollmentsThisMonth(userId);

        Double rawRevenue = enrollmentRepository.sumRevenueByTeacherId(userId);
        BigDecimal totalRevenue = rawRevenue != null ? BigDecimal.valueOf(rawRevenue) : BigDecimal.ZERO;

        long totalCourses = courseRepository.countByTeacher_TeacherId(userId);
        double avgRating  = 4.8; // TODO: từ ReviewRepository

        List<CourseResponse> courseResponses = getMyCourses(userId);

        return TeacherDashboardResponse.builder()
                .totalStudents(totalStudents)
                .enrollmentsThisMonth(enrollmentsThisMonth)
                .totalRevenue(totalRevenue)
                .avgRating(avgRating)
                .totalCourses(totalCourses)
                .earningsChange("+18% from last month")
                .studentsChange("+" + enrollmentsThisMonth + " ↑")
                .revenueChange("+15% ↑")
                .ratingChange("+0.2 ↑")
                .courses(courseResponses)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  (Cũ) getMyCourses
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<CourseResponse> getMyCourses(Integer userId) {
        List<Course> courses = courseRepository.findByTeacher_TeacherIdOrderByCreatedAtDesc(userId);
        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  (Mới) getOverview – 4 thẻ số liệu trên màn Home
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public TeacherOverviewResponse getOverview(Integer userId) {

        // Lấy teacher từ userId (bảng users.id)
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found for userId=" + userId));
        Integer teacherId = teacher.getTeacherId();

        // Tên hiển thị & initials
        String fullName = (teacher.getLastName() + " " + teacher.getFirstName()).trim();
        String initials = TimeAgoUtil.buildInitials(fullName);

        // Số khoá học active
        long totalActiveCourses = courseRepository.countByTeacher_TeacherId(teacherId);

        // Tổng học sinh distinct
        long totalStudents = enrollmentRepository.countDistinctStudentsByTeacherId(teacherId);

        // Số bài chờ chấm (assignment chưa có trong gradebook)
        long pendingGrading = submissionRepository.countPendingGradingByTeacherId(teacherId);

        // Điểm trung bình
        Double avgScore = gradebookRepository.avgScoreByTeacherId(teacherId);
        double safeScore = (avgScore != null) ? avgScore : 0.0;
        avgScore = (double) Math.round(safeScore * 10) / 10;

        return TeacherOverviewResponse.builder()
                .teacherName(fullName)
                .avatarInitials(initials)
                .totalActiveCourses(totalActiveCourses)
                .totalStudents(totalStudents)
                .pendingGrading(pendingGrading)
                .avgScore(avgScore)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  (Mới) getRecentActivities – Hoạt động gần đây
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<RecentActivityResponse> getRecentActivities(Integer userId, int limit) {

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found for userId=" + userId));
        Integer teacherId = teacher.getTeacherId();

        List<RecentActivityResponse> activities = new ArrayList<>();

        // ── Nguồn 1: Bài nộp (submission) ──────────────────────────────────
        List<Object[]> submissions = submissionRepository
                .findRecentSubmissionsByTeacherId(teacherId, limit);

        for (Object[] row : submissions) {
            Integer studentId    = toInteger(row[1]);
            Integer courseId     = toInteger(row[2]);
            String  courseTitle  = toString(row[3]);
            String  type         = toString(row[4]);
            LocalDateTime submittedAt = toLocalDateTime(row[5]);

            String studentName  = getStudentName(studentId);
            String activityType = "assignment".equalsIgnoreCase(type)
                    ? "SUBMITTED_ASSIGNMENT" : "SUBMITTED";
            String description  = "assignment".equalsIgnoreCase(type)
                    ? "nộp bài tự luận" : "nộp bài";

            activities.add(RecentActivityResponse.builder()
                    .studentName(studentName)
                    .studentInitials(TimeAgoUtil.buildInitials(studentName))
                    .activityType(activityType)
                    .description(description)
                    .courseId(courseId)
                    .courseTitle(courseTitle)
                    .occurredAt(submittedAt)
                    .timeAgo(TimeAgoUtil.calculate(submittedAt))
                    .build());
        }

        // ── Nguồn 2: Đã chấm điểm (gradebook) ─────────────────────────────
        List<Object[]> graded = gradebookRepository
                .findRecentGradedByTeacherId(teacherId, limit);

        for (Object[] row : graded) {
            Integer studentId   = toInteger(row[1]);
            Integer courseId    = toInteger(row[2]);
            String  courseTitle = toString(row[3]);
            Double  score       = toDouble(row[4]);
            LocalDateTime gradedAt = toLocalDateTime(row[5]);

            String studentName = getStudentName(studentId);
            String description = "nhận điểm " + (score != null
                    ? (score % 1 == 0 ? String.valueOf(score.intValue()) : String.valueOf(score))
                    : "?");

            activities.add(RecentActivityResponse.builder()
                    .studentName(studentName)
                    .studentInitials(TimeAgoUtil.buildInitials(studentName))
                    .activityType("RECEIVED_GRADE")
                    .description(description)
                    .courseId(courseId)
                    .courseTitle(courseTitle)
                    .occurredAt(gradedAt)
                    .timeAgo(TimeAgoUtil.calculate(gradedAt))
                    .build());
        }

        // ── Nguồn 3: Câu hỏi (discussion, MongoDB) ─────────────────────────
        // Lấy danh sách courseId của teacher để query MongoDB
        List<Integer> courseIds = courseRepository
                .findByTeacher_TeacherIdOrderByCreatedAtDesc(teacherId)
                .stream()
                .map(Course::getCourseId)
                .collect(Collectors.toList());

        if (!courseIds.isEmpty()) {
            List<Discussion> discussions = discussionRepository
                    .findRecentStudentDiscussionsByCourseIds(
                            courseIds,
                            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));

            for (Discussion d : discussions) {
                String studentName = getStudentName(d.getAuthorId());
                // Tìm tên khoá học từ courseId
                String courseTitle = courseRepository.findById(d.getCourseId())
                        .map(Course::getTitle)
                        .orElse("Khoá học #" + d.getCourseId());

                activities.add(RecentActivityResponse.builder()
                        .studentName(studentName)
                        .studentInitials(TimeAgoUtil.buildInitials(studentName))
                        .activityType("ASKED_QUESTION")
                        .description("đặt câu hỏi")
                        .courseId(d.getCourseId())
                        .courseTitle(courseTitle)
                        .occurredAt(d.getCreatedAt())
                        .timeAgo(TimeAgoUtil.calculate(d.getCreatedAt()))
                        .build());
            }
        }

        // ── Sắp xếp tất cả theo thời gian giảm dần, lấy `limit` đầu ───────
        activities.sort(Comparator.comparing(
                RecentActivityResponse::getOccurredAt,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return activities.stream().limit(limit).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  (Mới) getWeeklyActivity – Biểu đồ tuần
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public WeeklyActivityResponse getWeeklyActivity(Integer userId) {

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found for userId=" + userId));
        Integer teacherId = teacher.getTeacherId();

        // Tính khoảng tuần hiện tại (T2 → CN)
        LocalDate today       = LocalDate.now();
        LocalDate mondayThis  = today.with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDate sundayThis  = mondayThis.plusDays(6);
        LocalDateTime startThis = mondayThis.atStartOfDay();
        LocalDateTime endThis   = sundayThis.atTime(LocalTime.MAX);

        // Tuần trước
        LocalDateTime startLast = startThis.minusWeeks(1);
        LocalDateTime endLast   = endThis.minusWeeks(1);

        // Query DB: đếm theo ngày trong tuần (DOW: 0=CN, 1=T2 ... 6=T7 — PostgreSQL)
        List<Object[]> rows = submissionRepository
                .countSubmissionsPerDayOfWeek(teacherId, startThis, endThis);

        // Build map: dayOfWeek → count
        Map<Integer, Long> dowMap = new HashMap<>();
        for (Object[] row : rows) {
            int dow   = ((Number) row[0]).intValue(); // PostgreSQL: 0=CN,1=T2..6=T7
            long cnt  = ((Number) row[1]).longValue();
            dowMap.put(dow, cnt);
        }

        // Cấu hình nhãn hiển thị: T2=dow1 ... T7=dow6, CN=dow0
        // Thứ tự hiển thị: T2, T3, T4, T5, T6, T7, CN
        List<WeeklyActivityResponse.DailyCount> daily = new ArrayList<>();
        int[] pgDows    = {1, 2, 3, 4, 5, 6, 0};
        String[] labels = {"T2","T3","T4","T5","T6","T7","CN"};

        long totalThis = 0;
        for (int i = 0; i < 7; i++) {
            long cnt = dowMap.getOrDefault(pgDows[i], 0L);
            totalThis += cnt;
            daily.add(WeeklyActivityResponse.DailyCount.builder()
                    .dayLabel(labels[i])
                    .dayOfWeek(pgDows[i])
                    .count(cnt)
                    .build());
        }

        // Tuần trước – chỉ cần tổng để tính %
        long totalLast = submissionRepository
                .countSubmissionsInRange(teacherId, startLast, endLast);

        double changePercent = 0.0;
        if (totalLast > 0) {
            changePercent = Math.round(((double)(totalThis - totalLast) / totalLast) * 1000.0) / 10.0;
        } else if (totalThis > 0) {
            changePercent = 100.0;
        }

        return WeeklyActivityResponse.builder()
                .dailySubmissions(daily)
                .totalThisWeek(totalThis)
                .totalLastWeek(totalLast)
                .weeklyChangePercent(changePercent)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  (Mới) getTasks – Cần làm ngay
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public TeacherTaskResponse getTasks(Integer userId) {

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found for userId=" + userId));
        Integer teacherId = teacher.getTeacherId();

        List<TeacherTaskResponse.TaskItem> tasks = new ArrayList<>();

        // ── Task: Chấm bài tự luận ──────────────────────────────────────────
        List<Object[]> pendingRows = submissionRepository
                .findPendingGradingGroupedByCourse(teacherId);

        LocalDate today = LocalDate.now();

        for (Object[] row : pendingRows) {
            Integer courseId    = toInteger(row[0]);
            String  courseTitle = toString(row[1]);
            long    count       = ((Number) row[2]).longValue();

            String title = "Chấm " + count + " bài tự luận";

            tasks.add(TeacherTaskResponse.TaskItem.builder()
                    .taskType("GRADE_ASSIGNMENT")
                    .title(title)
                    .courseId(courseId)
                    .courseTitle(courseTitle)
                    .dueDate(today)   // mặc định coi là hôm nay để ưu tiên
                    .isUrgent(count >= 3)   // >= 3 bài → đánh dấu Gấp
                    .itemCount(count)
                    .build());
        }

        return TeacherTaskResponse.builder()
                .pendingCount(tasks.size())
                .tasks(tasks)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Lấy tên đầy đủ học sinh từ studentId (tìm trong bảng student). */
    private String getStudentName(Integer studentId) {
        if (studentId == null) return "Học sinh";
        return studentRepository.findById(studentId)
                .map(s -> (s.getLastName() + " " + s.getFirstName()).trim())
                .orElse("Học sinh #" + studentId);
    }

    private CourseResponse mapToCourseResponse(Course course) {
        String teacherName = course.getTeacher() != null
                ? course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName()
                : "Unknown Teacher";
        String categoryName = course.getCategory() != null
                ? course.getCategory().getCategoryName()
                : "Uncategorized";

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .imageUrl(course.getImageUrl())
                .price(course.getPrice())
                .createdAt(course.getCreatedAt())
                .archiveStatus(course.getArchiveStatus())
                .teacherName(teacherName)
                .categoryName(categoryName)
                .build();
    }

    // ── Type-cast helpers cho Object[] từ native query ───────────────────────
    private Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }

    private String toString(Object o) {
        return o == null ? "" : o.toString();
    }

    private Double toDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Double d) return d;
        if (o instanceof Number n) return n.doubleValue();
        return Double.parseDouble(o.toString());
    }

    private LocalDateTime toLocalDateTime(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDateTime ldt) return ldt;
        // PostgreSQL trả về java.sql.Timestamp
        if (o instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        return null;
    }
}