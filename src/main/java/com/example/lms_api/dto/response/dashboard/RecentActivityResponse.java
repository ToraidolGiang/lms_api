package com.example.lms_api.dto.response.dashboard;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Response cho 1 mục trong danh sách "Hoạt động gần đây"
 *
 * Màn hình hiển thị:
 *   [NA] Nguyễn Minh Anh  nộp bài tự luận
 *                         INT101  · 2 phút trước
 *
 *   [LC] Lê Thị Cẩm       nhận điểm 88
 *                         WEB301 · 1 giờ trước
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityResponse {

    // ── Học sinh ────────────────────────────────────────────
    /** Tên đầy đủ: "Nguyễn Minh Anh" */
    private String studentName;

    /** Viết tắt 2 ký tự cho avatar: "NA" */
    private String studentInitials;

    // ── Loại hoạt động ──────────────────────────────────────
    /**
     * Loại hoạt động:
     *  SUBMITTED_ASSIGNMENT – nộp bài tự luận
     *  RECEIVED_GRADE       – nhận điểm XX
     *  ASKED_QUESTION       – đặt câu hỏi
     *  LATE_SUBMISSION      – nộp bài trễ hạn
     */
    private String activityType;

    /**
     * Câu mô tả ngắn hiển thị trên UI:
     *  "nộp bài tự luận"
     *  "nhận điểm 88"
     *  "đặt câu hỏi"
     *  "nộp bài trễ hạn"
     */
    private String description;

    // ── Khoá học ────────────────────────────────────────────
    /**
     * ID khoá học (dùng thay courseCode).
     * Ví dụ: 101 → client hiển thị dạng "COURSE-101" nếu muốn
     */
    private Integer courseId;

    /**
     * Tên khoá học ngắn gọn (title của Course)
     * Ví dụ: "INT101 – Lập trình Java"
     */
    private String courseTitle;

    // ── Thời gian ────────────────────────────────────────────
    /** Thời điểm xảy ra hoạt động (raw) */
    private LocalDateTime occurredAt;

    /**
     * Chuỗi thời gian tương đối để hiển thị:
     * "2 phút trước", "1 giờ trước", "2 giờ trước"
     */
    private String timeAgo;
}
