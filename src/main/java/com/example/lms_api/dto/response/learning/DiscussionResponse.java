package com.example.lms_api.dto.response.learning;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DiscussionResponse {
    private String id;
    private String lessonId;
    private Integer authorId;
    private String authorName; // Móc từ SQL (User/Student table)
    private String authorRole; // STUDENT / TEACHER
    private String title;
    private String content;
    private String codeSnippet;
    private int upvotes;
    private int replyCount;
    private LocalDateTime createdAt;
    private List<ReplyResponse> replies;

    @Data
    @Builder
    public static class ReplyResponse {
        private Integer replyId;
        private Integer authorId;
        private String authorName;
        private String authorRole;
        private String content;
        private boolean isAccepted;
        private int upvotes;
        private LocalDateTime createdAt;
    }
}