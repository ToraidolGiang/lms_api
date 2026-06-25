package com.example.lms_api.util;

import com.example.lms_api.entity.Student;
import com.example.lms_api.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility tập trung để lấy thông tin người dùng hiện tại từ JWT.
 * Thay thế các method private trùng lặp (currentUserId, currentStudent,
 * getCurrentStudentId) trong nhiều ServiceImpl.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final StudentRepository studentRepository;

    /**
     * Lấy userId (Integer) của người đang đăng nhập từ JWT token.
     * Dùng cho các chức năng chỉ cần userId (Notification, Post...).
     */
    public Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthenticated");
        }
        return Integer.parseInt(auth.getName());
    }

    /**
     * Lấy entity Student của người đang đăng nhập.
     * Dùng cho các chức năng cần toàn bộ thông tin Student (Payment, Review...).
     */
    public Student getCurrentStudent() {
        Integer userId = getCurrentUserId();
        return studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Student profile not found for userId=" + userId));
    }

    /**
     * Lấy studentId (Integer) của người đang đăng nhập.
     * Dùng cho các chức năng chỉ cần studentId (Learning...).
     */
    public Integer getCurrentStudentId() {
        return getCurrentStudent().getStudentId();
    }
}
