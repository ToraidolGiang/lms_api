package com.example.lms_api.dto.response.dashboard;

import lombok.*;

/**
 * Response cho GET /api/teacher-dashboard/overview
 *
 * Tương ứng với phần header + 4 thẻ số liệu trên màn hình Home:
 *  - Tên giáo viên, initials avatar
 *  - 3  Khoá học · đang dạy
 *  - 114 Học sinh · tổng
 *  - 10  Chờ chấm · bài
 *  - 76.4 Điểm TB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherOverviewResponse {

    // ── Header ────────────────────────────────────────────────
    /** Họ tên đầy đủ: "Nguyễn Hoàng" */
    private String teacherName;

    /** 2 ký tự viết tắt dùng cho avatar: "TH" */
    private String avatarInitials;

    // ── Stats cards ──────────────────────────────────────────
    /** Số khoá học đang active (is_active=true, is_deleted=false) của giáo viên */
    private long totalActiveCourses;

    /** Tổng số học sinh distinct đã enroll vào khoá học của giáo viên */
    private long totalStudents;

    /**
     * Số bài nộp (submission type='assignment') chưa được chấm
     * (tức là chưa có bản ghi trong gradebook)
     */
    private long pendingGrading;

    /**
     * Điểm trung bình (AVG gradebook.score) của tất cả bài đã chấm
     * thuộc các khoá học của giáo viên
     */
    private double avgScore;
}
