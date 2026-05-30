package com.example.lms_api.controller;

import com.example.lms_api.dto.request.LoginRequest;
import com.example.lms_api.dto.request.RefreshTokenRequest;
import com.example.lms_api.dto.request.RegisterRequest;
import com.example.lms_api.dto.response.ApiResponse;
import com.example.lms_api.dto.response.AuthResponse;
import com.example.lms_api.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthServiceImpl authServiceImpl;

    // ── POST /api/auth/register ───────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse data = authServiceImpl.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng ký thành công", data));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse data = authServiceImpl.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", data));
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse data = authServiceImpl.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok("Tạo lại token thành công", data));
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        authServiceImpl.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Đăng xuất thành công"));
    }
}