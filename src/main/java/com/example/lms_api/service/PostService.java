package com.example.lms_api.service;

import com.example.lms_api.dto.request.CreateCommentRequest;
import com.example.lms_api.dto.request.CreatePostRequest;
import com.example.lms_api.dto.request.UpdatePostRequest;
import com.example.lms_api.dto.response.communit_response.CommentResponse;
import com.example.lms_api.dto.response.communit_response.CommunityActionResponse;
import com.example.lms_api.dto.response.communit_response.PostDetailResponse;
import com.example.lms_api.dto.response.communit_response.PostResponse;
import com.example.lms_api.dto.response.communit_response.CommunityStatsResponse;
import java.util.List;

public interface PostService {

    List<PostResponse> getPosts(String category, String query, String sortBy, int page, int size);

    PostDetailResponse getPostDetail(String id, String currentUserId);

    PostResponse createPost(CreatePostRequest request, String userId);

    CommunityActionResponse deletePost(String postId, String currentUserId, boolean isAdmin);

    CommunityActionResponse toggleLike(String postId, String currentUserId);

    CommentResponse addComment(String postId, CreateCommentRequest request, String currentUserId);

    CommunityActionResponse deleteComment(String postId, String commentId, String currentUserId, boolean isAdmin);

    PostResponse updatePost(String postId, UpdatePostRequest request, String currentUserId, boolean isAdmin);

    CommunityStatsResponse getCommunityStats();

    // 🌟 BỔ SUNG: Khai báo hàm thao tác Ghim/Bỏ ghim
    PostResponse togglePin(String postId, String currentUserId, boolean canPin);
}