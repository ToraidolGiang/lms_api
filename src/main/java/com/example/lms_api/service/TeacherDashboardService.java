package com.example.lms_api.service;

import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.TeacherDashboardResponse;
import com.example.lms_api.dto.response.dashboard.RecentActivityResponse;
import com.example.lms_api.dto.response.dashboard.TeacherOverviewResponse;
import com.example.lms_api.dto.response.dashboard.TeacherTaskResponse;
import com.example.lms_api.dto.response.dashboard.WeeklyActivityResponse;

import java.util.List;

public interface TeacherDashboardService {

    /**
     * (Cũ) Trả về toàn bộ dữ liệu dashboard:
     * stats + danh sách khoá học + 5 review gần nhất
     *
     * @param userId  — userId từ JWT (subject), là id trong bảng users
     */
    TeacherDashboardResponse getDashboard(Integer userId);

    /**
     * (Cũ) Chỉ lấy danh sách khoá học của teacher (tab My Courses)
     */
    List<CourseResponse> getMyCourses(Integer userId);

    // ── API mới theo UI ──────────────────────────────────────────────────────

    /**
     * GET /api/teacher-dashboard/overview
     *
     * Trả về tên giáo viên + 4 thẻ số liệu trên màn Home:
     *   - Số khoá học đang dạy
     *   - Tổng học sinh
     *   - Số bài chờ chấm
     *   - Điểm trung bình
     *
     * @param userId userId lấy từ JWT (bảng users.id)
     */
    TeacherOverviewResponse getOverview(Integer userId);

    /**
     * GET /api/teacher-dashboard/recent-activities?limit=10
     *
     * Trả về danh sách hoạt động gần đây gộp từ 3 nguồn:
     *   1. Submission (nộp bài, nộp trễ)
     *   2. Gradebook  (nhận điểm)
     *   3. Discussion (đặt câu hỏi)
     * Sắp xếp theo thời gian giảm dần.
     *
     * @param userId userId từ JWT
     * @param limit  số lượng tối đa mỗi nguồn (mặc định 5)
     */
    List<RecentActivityResponse> getRecentActivities(Integer userId, int limit);

    /**
     * GET /api/teacher-dashboard/weekly-activity
     *
     * Trả về biểu đồ số bài nộp từng ngày T2→CN
     * + phần trăm thay đổi so với tuần trước.
     *
     * @param userId userId từ JWT
     */
    WeeklyActivityResponse getWeeklyActivity(Integer userId);

    /**
     * GET /api/teacher-dashboard/tasks
     *
     * Trả về danh sách task "Cần làm ngay":
     *   - GRADE_ASSIGNMENT: khoá học còn bài tự luận chưa chấm
     *
     * @param userId userId từ JWT
     */
    TeacherTaskResponse getTasks(Integer userId);
}