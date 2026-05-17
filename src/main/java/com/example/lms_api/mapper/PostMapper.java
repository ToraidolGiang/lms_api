package com.example.lms_api.mapper;

import com.example.lms_api.dto.response.communit_response.CommentResponse;
import com.example.lms_api.dto.response.communit_response.PostDetailResponse;
import com.example.lms_api.dto.response.communit_response.PostResponse;
import com.example.lms_api.entity.Comment;
import com.example.lms_api.entity.Post;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostMapper {

    public PostResponse toResponse(Post post) {
        if (post == null) return null;

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
                post.getAuthorName(),
                post.getAuthorRole()
        );
    }

    public PostDetailResponse toDetailResponse(Post post, String currentUserId) {
        PostResponse base = toResponse(post);
        if (base == null) return null;

        PostDetailResponse detail = new PostDetailResponse();
        detail.setId(base.getId());
        detail.setTitle(base.getTitle());
        detail.setContent(base.getContent());
        detail.setCategory(base.getCategory());
        detail.setType(base.getType());
        detail.setViews(base.getViews());
        detail.setLikes(base.getLikes());
        detail.setCommentsCount(base.getCommentsCount());
        detail.setCreatedAt(base.getCreatedAt());
        detail.setUpdatedAt(base.getUpdatedAt());
        detail.setUserId(base.getUserId());
        detail.setAuthorName(base.getAuthorName());
        detail.setAuthorRole(base.getAuthorRole());


        boolean likedByMe = currentUserId != null
                && post.getLikes() != null
                && post.getLikes().contains(currentUserId);
        detail.setLikedByMe(likedByMe);

        List<CommentResponse> comments = new ArrayList<>();
        if (post.getComments() != null) {
            for (Comment c : post.getComments()) {
                comments.add(new CommentResponse(
                        c.getCommentId(),
                        c.getUserId(),
                        c.getAuthorName(),
                        c.getContent(),
                        c.getCreatedAt()
                ));
            }
        }
        detail.setComments(comments);

        return detail;
    }

    private String createTitleFromContent(String content) {
        if (content == null || content.trim().isEmpty()) return "Bài viết cộng đồng";
        String trimmed = content.trim();
        return trimmed.length() <= 50 ? trimmed : trimmed.substring(0, 50) + "...";
    }
}
