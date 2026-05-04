package com.example.lms_api.service;

import com.example.lms_api.dto.request.LoginRequest;
import com.example.lms_api.dto.request.RefreshTokenRequest;
import com.example.lms_api.dto.response.AuthResponse;

public interface AuthService {

    // ── Đăng nhập ────────────────────────────────────────────
    AuthResponse login(LoginRequest request);

    // ── Refresh Token ─────────────────────────────────────────
    AuthResponse refresh(RefreshTokenRequest request);

    // ── Đăng xuất ─────────────────────────────────────────────
    void logout(String refreshToken);

}