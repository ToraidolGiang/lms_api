package com.example.lms_api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {
    private String id;
    private String title;
    // Sửa thuộc tính body thành message, thêm link
    private String message;
    private String link;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
}
