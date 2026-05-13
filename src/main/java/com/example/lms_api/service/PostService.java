package com.example.lms_api.service;

import com.example.lms_api.dto.request.CreatePostRequest;
import com.example.lms_api.dto.response.PostResponse;

import java.util.List;

public interface PostService {

    List<PostResponse> getPosts(String category);

    PostResponse getPostDetail(String id);

    PostResponse createPost(CreatePostRequest request, String userId);
}