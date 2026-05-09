package com.example.lms_api.controller;

import com.example.lms_api.dto.response.NotificationResponse;
import com.example.lms_api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService service;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        // Lấy userId từ JWT context đã có của bạn
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.getNotificationsForUser(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
