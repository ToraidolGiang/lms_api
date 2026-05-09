package com.example.lms_api.service;

import com.example.lms_api.entity.Notification;
import com.example.lms_api.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NotificationService {
    @Autowired
    private NotificationRepository repository;

    public List<Notification> getNotificationsForUser(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long id) {
        Notification n = repository.findById(id).orElseThrow();
        n.setRead(true);
        repository.save(n);
    }
}
