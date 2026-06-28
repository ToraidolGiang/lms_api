package com.example.lms_api.controller;

import com.example.lms_api.dto.request.CreateCommentRequest;
import com.example.lms_api.dto.request.CreatePostRequest;
import com.example.lms_api.dto.response.communit_response.CommentResponse;
import com.example.lms_api.dto.response.communit_response.CommunityActionResponse;
import com.example.lms_api.dto.response.communit_response.PostDetailResponse;
import com.example.lms_api.dto.response.communit_response.PostResponse;
import com.example.lms_api.dto.response.communit_response.CommunityStatsResponse;
import com.example.lms_api.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/stats")
    public ResponseEntity<CommunityStatsResponse> getCommunityStats() {
        return ResponseEntity.ok(postService.getCommunityStats());
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.getPosts(category, query, sortBy, page, size, currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable String id,
            Authentication authentication
    ) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.getPostDetail(id, currentUserId));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam(required = false) String category,
            @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {
        String userId = requireUserId(authentication);
        if (category != null && !category.trim().isEmpty()) {
            request.setCategory(category);
            request.setType(category);
        } else {
            if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
                request.setCategory("Thảo luận");
            }
            if (request.getType() == null || request.getType().trim().isEmpty()) {
                request.setType(request.getCategory());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request, userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<CommunityActionResponse> deletePost(
            @PathVariable String postId,
            Authentication authentication
    ) {
        String userId = requireUserId(authentication);
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        return ResponseEntity.ok(postService.deletePost(postId, userId, isAdmin));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<CommunityActionResponse> toggleLike(
            @PathVariable String postId,
            Authentication authentication
    ) {
        String userId = requireUserId(authentication);
        return ResponseEntity.ok(postService.toggleLike(postId, userId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String postId,
            @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        String userId = requireUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.addComment(postId, request, userId));
    }

    // 🌟 BỔ SUNG ENDPOINT: Định tuyến PUT cập nhật nội dung bình luận
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        String userId = requireUserId(authentication);
        return ResponseEntity.ok(postService.updateComment(postId, commentId, request, userId));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommunityActionResponse> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            Authentication authentication
    ) {
        String userId = requireUserId(authentication);
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        return ResponseEntity.ok(postService.deleteComment(postId, commentId, userId, isAdmin));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable String postId,
            @RequestBody com.example.lms_api.dto.request.UpdatePostRequest request,
            Authentication authentication
    ) {
        String userId = requireUserId(authentication);
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        return ResponseEntity.ok(postService.updatePost(postId, request, userId, isAdmin));
    }



    private String requireUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Chưa đăng nhập"
            );
        }
        return authentication.getName();
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) return false;
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}