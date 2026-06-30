package com.example.lms_api.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

@Document(collection = "discussions")
@CompoundIndexes({
    @CompoundIndex(name = "course_lesson_created_idx", def = "{'courseId': 1, 'lessonId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "course_role_created_idx", def = "{'courseId': 1, 'authorRole': 1, 'createdAt': -1}")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Discussion {

    @Id
    private String id;

    @Field("discussionId")
    private Integer discussionId; // ID tự tăng tự chế (nếu cần) hoặc để null

    @Field("courseId")
    private Integer courseId;

    @Field("lessonId")
    private String lessonId;

    @Field("authorId")
    private Integer authorId; // ID của Student hoặc Teacher

    @Field("authorRole")
    private String authorRole; // "STUDENT" hoặc "TEACHER"

    @Field("title")
    private String title;

    @Field("content")
    private String content;

    @Field("codeSnippet")
    private String codeSnippet;

    @Field("tags")
    private List<String> tags;

    @Field("replies")
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

    @Field("views")
    private int views;

    @Field("upvotes")
    private int upvotes;

    @Field("isPinned")
    private boolean isPinned;

    @Field("isSolved")
    private boolean isSolved;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    // Class lồng cho các câu trả lời (Replies)
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Reply {
        @Field("replyId")
        private Integer replyId;

        @Field("authorId")
        private Integer authorId;

        @Field("authorRole")
        private String authorRole;

        @Field("content")
        private String content;

        @Field("isAccepted")
        private boolean isAccepted;

        @Field("upvotes")
        private int upvotes;

        @Field("createdAt")
        private LocalDateTime createdAt;
    }
}