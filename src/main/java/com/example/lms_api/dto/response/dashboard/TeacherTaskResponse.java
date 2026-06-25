package com.example.lms_api.dto.response.dashboard;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Response cho GET /api/teacher-dashboard/tasks
 *
 * Màn hình "Cần làm ngay":
 *  [badge 3]
 *  🟠 Chấm 3 bài tự luận  [Gấp]
 *     INT101 · Hôm nay
 *
 * Task types được hỗ trợ:
 *  - GRADE_ASSIGNMENT: có bài tự luận chưa chấm
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTaskResponse {

    /** Tổng số task chưa hoàn thành (hiển thị trên badge đỏ) */
    private int pendingCount;

    /** Danh sách các task cần làm */
    private List<TaskItem> tasks;

    // ── Inner class ─────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskItem {

        /**
         * Loại task:
         *  GRADE_ASSIGNMENT – Chấm bài tự luận chưa được chấm
         */
        private String taskType;

        /**
         * Tiêu đề hiển thị:
         *  "Chấm 3 bài tự luận"
         */
        private String title;

        /** courseId của khoá học liên quan */
        private Integer courseId;

        /** Tên khoá học ngắn (course.title) */
        private String courseTitle;

        /**
         * Ngày đến hạn (null nếu không có deadline cụ thể).
         * Hiển thị: "Hôm nay" nếu dueDate == today
         */
        private LocalDate dueDate;

        /**
         * true nếu task cần xử lý gấp
         * (ví dụ: dueDate <= hôm nay, hoặc số bài > ngưỡng)
         */
        private boolean isUrgent;

        /** Số lượng bài cần xử lý (ví dụ: 3 bài chờ chấm) */
        private long itemCount;
    }
}
