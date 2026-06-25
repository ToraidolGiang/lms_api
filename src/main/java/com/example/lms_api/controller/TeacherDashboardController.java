package com.example.lms_api.controller;

import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.TeacherDashboardResponse;
import com.example.lms_api.dto.response.dashboard.*;
import com.example.lms_api.service.TeacherDashboardService;
import com.example.lms_api.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-dashboard")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService dashboardService;
    private final SecurityUtil            securityUtil;

    // ─────────────────────────────────────────────────────────────────────────
    //  (Cũ) Giữ lại để không break client đang dùng
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/teacher-dashboard/legacy/{userId}
     * @deprecated Dùng /overview thay thế
     */
    @Deprecated
    @GetMapping("/legacy/{userId}/dashboard")
    public ResponseEntity<TeacherDashboardResponse> getDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(dashboardService.getDashboard(userId));
    }

    /**
     * GET /api/teacher-dashboard/legacy/{userId}/courses
     * @deprecated Dùng /courses thay thế
     */
    @Deprecated
    @GetMapping("/legacy/{userId}/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(dashboardService.getMyCourses(userId));
    }

    /**
     * GET /api/teacher-dashboard/overview
     *
     * Trả về tên giáo viên + 4 thẻ số liệu trên màn Home:
     * <pre>
     *   {
     *     "teacherName": "Nguyễn Hoàng",
     *     "avatarInitials": "TH",
     *     "totalActiveCourses": 3,
     *     "totalStudents": 114,
     *     "pendingGrading": 10,
     *     "avgScore": 76.4
     *   }
     * </pre>
     *
     * Header: Authorization: Bearer {jwt}
     */
    @GetMapping("/overview")
    public ResponseEntity<TeacherOverviewResponse> getOverview() {
        Integer userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getOverview(userId));
    }

    /**
     * GET /api/teacher-dashboard/recent-activities?limit=10
     *
     * Danh sách hoạt động gần đây (nộp bài, nhận điểm, đặt câu hỏi).
     * Sắp xếp từ mới nhất đến cũ nhất.
     *
     * Query param:
     *   limit – số lượng tối đa (default 10, max 50)
     *
     * Header: Authorization: Bearer {jwt}
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<List<RecentActivityResponse>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        if (limit > 50) limit = 50;
        Integer userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getRecentActivities(userId, limit));
    }

    /**
     * GET /api/teacher-dashboard/weekly-activity
     *
     * Biểu đồ số bài nộp từng ngày trong tuần hiện tại + % so tuần trước.
     * <pre>
     *   {
     *     "dailySubmissions": [
     *       { "dayLabel": "T2", "dayOfWeek": 1, "count": 3 },
     *       ...
     *       { "dayLabel": "CN", "dayOfWeek": 0, "count": 0 }
     *     ],
     *     "totalThisWeek": 25,
     *     "totalLastWeek": 22,
     *     "weeklyChangePercent": 12.0
     *   }
     * </pre>
     *
     * Header: Authorization: Bearer {jwt}
     */
    @GetMapping("/weekly-activity")
    public ResponseEntity<WeeklyActivityResponse> getWeeklyActivity() {
        Integer userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getWeeklyActivity(userId));
    }

    /**
     * GET /api/teacher-dashboard/tasks
     *
     * Danh sách task "Cần làm ngay" + badge đếm.
     * <pre>
     *   {
     *     "pendingCount": 3,
     *     "tasks": [
     *       {
     *         "taskType": "GRADE_ASSIGNMENT",
     *         "title": "Chấm 3 bài tự luận",
     *         "courseId": 101,
     *         "courseTitle": "Lập trình Java cơ bản",
     *         "dueDate": "2026-06-25",
     *         "isUrgent": true,
     *         "itemCount": 3
     *       }
     *     ]
     *   }
     * </pre>
     *
     * Header: Authorization: Bearer {jwt}
     */
    @GetMapping("/tasks")
    public ResponseEntity<TeacherTaskResponse> getTasks() {
        Integer userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getTasks(userId));
    }
}