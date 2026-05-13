package com.example.lms_api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private String targetUserId;
    private String title;
    // Sửa thuộc tính body thành message, thêm link
    private String message;
    private String link;
    private String type;
}
