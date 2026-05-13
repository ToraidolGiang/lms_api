package com.example.lms_api.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class Comment {

    private String userId;

    private String content;

    private Instant createdAt;

    public Comment() {
    }

    public Comment(String userId, String content, Instant createdAt) {
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

}