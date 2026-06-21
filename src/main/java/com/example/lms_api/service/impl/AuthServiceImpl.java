package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.LoginRequest;
import com.example.lms_api.dto.request.RefreshTokenRequest;
import com.example.lms_api.dto.request.RegisterRequest;
import com.example.lms_api.dto.response.AuthResponse;
import com.example.lms_api.entity.RefreshTokenEntity;
import com.example.lms_api.entity.Student;
import com.example.lms_api.entity.Teacher;
import com.example.lms_api.entity.User;
import com.example.lms_api.exception.InvalidTokenException;
import com.example.lms_api.repository.RefreshTokenRepository;
import com.example.lms_api.repository.StudentRepository;
import com.example.lms_api.repository.TeacherRepository;
import com.example.lms_api.repository.UserRepository;
import com.example.lms_api.service.AuthService;
import com.example.lms_api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

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

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Xoá refresh token cũ
        refreshTokenRepository.deleteAllByUser(user);

        // Tạo tokens
        String accessToken  = jwtUtil.generateAccessToken(user.getId().toString(), user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());



        // Query teacherId hoặc studentId tùy role
        Integer teacherId = null;
        Integer studentId = null;

        if (user.getRole() == User.Role.TEACHER) {
            teacherId = teacherRepository.findByUserId(user.getId())
                    .map(Teacher::getTeacherId)
                    .orElse(null);
        } else if (user.getRole() == User.Role.STUDENT) {
            studentId = studentRepository.findByUser_Id(user.getId())
                    .map(Student::getStudentId)
                    .orElse(null);
        }



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
                .role(user.getRole().name())
                .teacherId(teacherId)   // ← thêm
                .studentId(studentId)
                .imageUrl(user.getImageUrl())// ← thêm
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

        User user = storedToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user.getId().toString(), user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .isActive(user.isActive())
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

    @Override
    @Transactional
    public AuthResponse register(@NotNull RegisterRequest request) {

        // 1. Kiểm tra trùng username / email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được đăng ký");
        }

        // 2. Xác định role
        User.Role role;

        try {
            role = (request.getRole() != null && !request.getRole().isBlank())
                    ? User.Role.valueOf(request.getRole().toUpperCase())
                    : User.Role.STUDENT;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Role không hợp lệ. Chỉ chấp nhận: STUDENT, TEACHER"
            );
        }

        // Không cho phép ADMIN
        if (role == User.Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Không thể đăng ký tài khoản ADMIN"
            );
        }

        // 3. Gọi procedure
        userRepository.createUserWithProfile(
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                role.name()
        );

        // 4. Lấy lại user vừa tạo
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // 5. Generate token
        String accessToken = jwtUtil.generateAccessToken(
                user.getId().toString(),
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getId().toString()
        );


        Integer teacherId = null;
        Integer studentId = null;

        if (user.getRole() == User.Role.TEACHER) {
            teacherId = teacherRepository.findByUserId(user.getId())
                    .map(Teacher::getTeacherId)
                    .orElse(null);
        } else if (user.getRole() == User.Role.STUDENT) {
            studentId = studentRepository.findByUser_Id(user.getId())
                    .map(Student::getStudentId)
                    .orElse(null);
        }

        // 6. Response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .isActive(user.isActive())
                .role(user.getRole().name())
                .teacherId(teacherId)   // ← thêm
                .studentId(studentId)
                .build();
    }

}