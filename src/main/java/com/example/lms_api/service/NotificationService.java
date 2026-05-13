package com.example.lms_api.service;

import com.example.lms_api.dto.request.NotificationRequest;
import com.example.lms_api.dto.response.NotificationResponse;
import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    List<NotificationResponse> getMyNotifications();
    void markAsRead(String notificationId); // Tính năng chuẩn thực tế
}