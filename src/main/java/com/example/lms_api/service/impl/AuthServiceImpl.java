package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.LoginRequest;
import com.example.lms_api.dto.request.RefreshTokenRequest;
import com.example.lms_api.dto.response.AuthResponse;
import com.example.lms_api.entity.RefreshTokenEntity;
import com.example.lms_api.entity.UserEntity;
import com.example.lms_api.exception.GlobalExceptionHandler.*;
import com.example.lms_api.exception.InvalidTokenException;
import com.example.lms_api.repository.RefreshTokenRepository;
import com.example.lms_api.repository.UserRepository;
import com.example.lms_api.service.AuthService;
import com.example.lms_api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    // ── Đăng nhập ────────────────────────────────────────────
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Xoá refresh token cũ
        refreshTokenRepository.deleteAllByUser(user);

        // Tạo tokens
        String accessToken  = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Lưu refresh token vào Neon DB
        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(Instant.now().plusMillis(jwtUtil.getRefreshExpirationMs()))
                .build());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    // ── Refresh Token ─────────────────────────────────────────
    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshTokenEntity storedToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new InvalidTokenException("Refresh token đã hết hạn, vui lòng đăng nhập lại");
        }

        UserEntity user = storedToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    // ── Đăng xuất ─────────────────────────────────────────────
    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}