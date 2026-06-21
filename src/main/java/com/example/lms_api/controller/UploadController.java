package com.example.lms_api.controller;

import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.UserResponse;
import com.example.lms_api.service.CloudinaryService;
import com.example.lms_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;
    private final UserService       userService;

    // ── Upload avatar + lưu URL vào bảng users ────────────────
    // POST /api/upload/avatar
    // Trả về UserResponse (kèm imageUrl mới) để client cập nhật UI ngay
    @PostMapping("/avatar")
    public ResponseEntity<UserResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file) throws IOException {

        // 1. Upload lên Cloudinary → lấy URL
        String imageUrl = cloudinaryService.uploadImage(file, "lms/avatars");

        // 2. Lưu URL vào bảng users (cột ImageUrl)
        UserResponse response = userService.updateAvatar(imageUrl);

        return ResponseEntity.ok(response);
    }

    // ── Upload thumbnail khóa học (giữ nguyên) ────────────────
    @PostMapping("/course-thumbnail")
    public ResponseEntity<CourseResponse> uploadCourseThumbnail(
            @RequestParam("file") MultipartFile file) throws IOException {

        String url = cloudinaryService.uploadImage(file, "lms/thumbnails");

        // Trả về CourseResponse chỉ có imageUrl — Android lấy được url
        CourseResponse response = CourseResponse.builder()
                .imageUrl(url)
                .build();

        return ResponseEntity.ok(response);
    }

    // ── Upload file bài tập (giữ nguyên) ─────────────────────
    @PostMapping("/assignment")
    public ResponseEntity<String> uploadAssignment(
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = cloudinaryService.uploadImage(file, "lms/assignments");
        return ResponseEntity.ok(url);
    }
}