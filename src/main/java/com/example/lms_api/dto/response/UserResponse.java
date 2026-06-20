package com.example.lms_api.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String  email;
    private String  username;
    private String  imageUrl;
    private String  role;
}
