package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.NotificationRequest;
import com.example.lms_api.dto.response.NotificationResponse;
import com.example.lms_api.entity.Notification;
import com.example.lms_api.mapper.NotificationMapper;
import com.example.lms_api.repository.NotificationRepository;
import com.example.lms_api.repository.UserRepository;
import com.example.lms_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    // KỸ THUẬT QUAN TRỌNG: Lấy userId của người đang gọi API từ JWT
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Transactional
    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        // Chỉ cần biến Request thành Document (mapper đã tự gán targetUserId vào userId rồi)
        Notification document = notificationMapper.toEntity(request);

        // Lưu thẳng vào MongoDB
        Notification saved = notificationRepository.save(document);

        return notificationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        // Chỉ lấy thông báo của ĐÚNG CÁI THẰNG đang cầm JWT
        String currentUserId = getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUserId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        String currentUserId = getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        // BẢO MẬT: Kiểm tra xem thông báo này có đúng là của thằng đang đăng nhập không
        if (!notification.getUserId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên thông báo này");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        System.out.println("DEBUG: Notification marked as read: " + notificationId);
    }
}