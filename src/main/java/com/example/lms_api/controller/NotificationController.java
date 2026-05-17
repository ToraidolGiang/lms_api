package com.example.lms_api.controller;

import com.example.lms_api.dto.request.NotificationRequest;
import com.example.lms_api.dto.response.NotificationResponse;
import com.example.lms_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 1. API: Lấy danh sách thông báo của TÔI
    // Bất kỳ ai cầm JWT hợp lệ gọi vào đây cũng chỉ xem được thông báo của chính họ.
    @GetMapping("/my-notifications")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    // 2. API: Đánh dấu đã đọc
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // 3. API: Tạo thông báo mới
    // (Thực tế API này thường được gọi ngầm bởi hệ thống hoặc Admin, bạn có thể phân quyền @PreAuthorize("hasRole('ADMIN')") sau này)
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }
}