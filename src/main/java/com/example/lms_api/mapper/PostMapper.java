package com.example.lms_api.mapper;

import com.example.lms_api.dto.response.PostResponse;
import com.example.lms_api.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }

        int likesCount = post.getLikes() == null ? 0 : post.getLikes().size();
        int commentsCount = post.getComments() == null ? 0 : post.getComments().size();

        String title = post.getTitle();
        if (title == null || title.trim().isEmpty()) {
            title = createTitleFromContent(post.getContent());
        }

        String category = post.getCategory();
        if (category == null || category.trim().isEmpty()) {
            category = post.getType();
        }

        return new PostResponse(
                post.getId(),
                title,
                post.getContent(),
                category,
                post.getType(),
                post.getViews(),
                likesCount,
                commentsCount,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getUserId(),
                post.getAuthorName()
        );
    }

    private String createTitleFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Bài viết cộng đồng";
        }

        String trimmed = content.trim();

        if (trimmed.length() <= 50) {
            return trimmed;
        }

        return trimmed.substring(0, 50) + "...";
    }
}