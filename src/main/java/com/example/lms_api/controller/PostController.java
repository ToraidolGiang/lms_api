package com.example.lms_api.controller;

import com.example.lms_api.dto.request.CreatePostRequest;
import com.example.lms_api.dto.response.PostResponse;
import com.example.lms_api.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(required = false) String category
    ) {
        List<PostResponse> posts = postService.getPosts(category);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable String id) {
        PostResponse post = postService.getPostDetail(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {
        String userId;

        if (authentication != null && authentication.isAuthenticated()) {
            userId = authentication.getName();
        } else {
            throw new RuntimeException("Bạn cần đăng nhập để tạo bài viết");
        }

        PostResponse createdPost = postService.createPost(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
}