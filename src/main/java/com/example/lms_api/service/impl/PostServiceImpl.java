package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CreatePostRequest;
import com.example.lms_api.dto.response.PostResponse;
import com.example.lms_api.entity.Post;
import com.example.lms_api.mapper.PostMapper;
import com.example.lms_api.repository.PostRepository;
import com.example.lms_api.service.PostService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    public List<PostResponse> getPosts(String category) {
        List<Post> posts;

        if (category != null && !category.trim().isEmpty()) {
            String value = category.trim();

            posts = postRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(value);

            if (posts.isEmpty()) {
                posts = postRepository.findByTypeIgnoreCaseOrderByCreatedAtDesc(value);
            }
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        }

        return posts.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Override
    public PostResponse getPostDetail(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết id = " + id));

        post.setViews(post.getViews() + 1);
        post.setUpdatedAt(Instant.now());

        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }

    @Override
    public PostResponse createPost(CreatePostRequest request, String userId) {
        validateCreatePostRequest(request);

        Instant now = Instant.now();

        String category = request.getCategory() != null && !request.getCategory().trim().isEmpty()
                ? request.getCategory().trim()
                : "general";

        String type = request.getType() != null && !request.getType().trim().isEmpty()
                ? request.getType().trim()
                : category;

        Post post = new Post();
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setCategory(category);
        post.setType(type);
        post.setViews(0);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        post.setUserId(userId);
        post.setAuthorName(userId);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }

    private void validateCreatePostRequest(CreatePostRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request không được null");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề không được để trống");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung không được để trống");
        }
    }
}