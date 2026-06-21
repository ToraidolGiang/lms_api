package com.example.lms_api.service;

import com.example.lms_api.dto.response.UserResponse;

public interface UserService {
    UserResponse getMyProfile();
    UserResponse updateAvatar(String imageUrl);
}