package com.example.lms_api.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class Comment {

    private String commentId; // format: cmt_<uuid>, unique toàn hệ thống

    private String userId;

    private String authorName;

    private String content;

    private String parentCommentId;

    private Instant createdAt;

    public Comment() {
    }

    public Comment(String commentId, String userId, String authorName, String content, String parentCommentId, Instant createdAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.authorName = authorName;
        this.content = content;
        this.parentCommentId = parentCommentId;
        this.createdAt = createdAt;
    }
}
