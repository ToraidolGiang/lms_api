package com.example.lms_api.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Integer userId;
    private String email;
    private String role;
    private boolean isActive;
    private Integer teacherId;  // null nếu không phải TEACHER
    private Integer studentId;
}