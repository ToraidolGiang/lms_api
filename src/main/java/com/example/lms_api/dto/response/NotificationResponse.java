package com.example.lms_api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String body;
    private String type;
    private boolean isRead;
    private String createdAt; // Format ISO string hoặc yyyy-MM-dd HH:mm:ss
}