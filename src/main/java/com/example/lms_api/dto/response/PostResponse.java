package com.example.lms_api.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
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

    public PostResponse() {
    }

    public PostResponse(String id, String title, String content, String category, String type,
                        int views, int likes, int commentsCount,
                        Instant createdAt, Instant updatedAt,
                        String userId, String authorName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.type = type;
        this.views = views;
        this.likes = likes;
        this.commentsCount = commentsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.authorName = authorName;
    }

}