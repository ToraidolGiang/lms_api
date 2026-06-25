package com.example.lms_api.dto.response.dashboard;

import lombok.*;
import java.util.List;

/**
 * Response cho GET /api/teacher-dashboard/weekly-activity
 *
 * Màn hình "Hoạt động tuần này" hiển thị:
 *  - Biểu đồ cột: số bài nộp mỗi ngày T2 → CN
 *  - Badge "+12%" so với tuần trước
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyActivityResponse {

    /**
     * Dữ liệu từng ngày trong tuần hiện tại (7 phần tử: T2→CN).
     * Mỗi phần tử gồm nhãn ngày + số bài nộp.
     */
    private List<DailyCount> dailySubmissions;

    /** Tổng bài nộp trong tuần hiện tại */
    private long totalThisWeek;

    /** Tổng bài nộp trong tuần trước */
    private long totalLastWeek;

    /**
     * Phần trăm thay đổi so với tuần trước (làm tròn 1 chữ số thập phân).
     * Ví dụ: 12.0 → hiển thị "+12%"
     */
    private double weeklyChangePercent;

    // ── Inner class ─────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyCount {
        /** Nhãn ngày hiển thị: "T2", "T3", "T4", "T5", "T6", "T7", "CN" */
        private String dayLabel;

        /** Ngày trong tuần: 2 (Thứ 2) → 8 (Chủ nhật, dùng 8 để phân biệt) */
        private int dayOfWeek;

        /** Số bài nộp trong ngày đó */
        private long count;
    }
}
