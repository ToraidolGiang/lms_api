package com.example.lms_api.dto.response.communit_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String commentId;
    private String userId;
    private String authorName;
    private String content;
    private String parentCommentId;
    private Instant createdAt;
}
