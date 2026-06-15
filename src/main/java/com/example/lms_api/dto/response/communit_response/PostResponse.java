package com.example.lms_api.dto.response.communit_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private String id;
    private String title;
    private String content;
    private String category;
    private String type;
    private int views;
    private int likes;
    private int commentsCount;
    private Instant createdAt;
    private Instant updatedAt;
    private String userId;
    private String authorName;
    private String authorRole;

    private List<String> tags;

    // 🌟 BỔ SUNG: Truyền cờ Ghim xuống cho Frontend xử lý giao diện

    private boolean pinned;
}