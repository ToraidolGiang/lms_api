package com.example.lms_api.service.impl;

import com.example.lms_api.dto.response.UserResponse;
import com.example.lms_api.entity.User;
import com.example.lms_api.repository.UserRepository;
import com.example.lms_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // ── Lấy userId từ JWT ─────────────────────────────────────
    private Integer getCurrentUserId() {
        String userIdStr = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return Integer.parseInt(userIdStr);
    }

    @Override
    public UserResponse getMyProfile() {
        return null;
    }

    // ── Cập nhật avatar ───────────────────────────────────────
    @Override
    @Transactional
    public UserResponse updateAvatar(String imageUrl) {
        Integer userId = getCurrentUserId();
        int updated = userRepository.updateImageUrl(userId, imageUrl);
        if (updated == 0) {
            throw new RuntimeException("Không tìm thấy user để cập nhật avatar");
        }
        // Reload lại để trả về data mới
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .imageUrl(user.getImageUrl())
                .role(user.getRole().name())
                .build();
    }
}