package com.example.lms_api.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Utility tính chuỗi thời gian tương đối kiểu tiếng Việt.
 * Ví dụ: "2 phút trước", "1 giờ trước", "3 ngày trước"
 */
public class TimeAgoUtil {

    private TimeAgoUtil() {}

    /**
     * Tính chuỗi thời gian tương đối so với thời điểm hiện tại.
     *
     * @param dateTime thời điểm cần tính
     * @return chuỗi tiếng Việt: "vừa xong", "X phút trước", "X giờ trước", "X ngày trước"
     */
    public static String calculate(LocalDateTime dateTime) {
        if (dateTime == null) return "không rõ";

        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(dateTime, now);

        if (seconds < 60) {
            return "vừa xong";
        }

        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        if (minutes < 60) {
            return minutes + " phút trước";
        }

        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) {
            return hours + " giờ trước";
        }

        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 7) {
            return days + " ngày trước";
        }

        long weeks = days / 7;
        if (weeks < 4) {
            return weeks + " tuần trước";
        }

        long months = ChronoUnit.MONTHS.between(dateTime, now);
        if (months < 12) {
            return months + " tháng trước";
        }

        long years = ChronoUnit.YEARS.between(dateTime, now);
        return years + " năm trước";
    }

    /**
     * Tính 2 ký tự viết tắt từ họ tên đầy đủ.
     * "Nguyễn Minh Anh" → "NA" (ký tự đầu tiên và ký tự đầu từ cuối)
     *
     * @param fullName họ tên đầy đủ
     * @return chuỗi 1-2 ký tự in hoa
     */
    public static String buildInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        // Lấy ký tự đầu của từ đầu tiên + ký tự đầu của từ cuối cùng
        String first = parts[0].substring(0, 1).toUpperCase();
        String last  = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return first + last;
    }
}
