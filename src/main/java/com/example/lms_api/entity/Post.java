package com.example.lms_api.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    private String title;

    private String content;

    private String category;

    private String type;

    private int views = 0;

    private List<String> likes = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    private String userId;

    private String authorName;
    private String authorRole;

    @Field("createdAt")
    private Instant createdAt;

    @Field("updatedAt")
    private Instant updatedAt;

    // 🌟 SỬA DÒNG NÀY: Đổi từ "isPinned" thành "pinned" để đồng bộ tuyệt đối với PostResponse và Front-End
    private boolean pinned = false;

    public Post() {
    }

}