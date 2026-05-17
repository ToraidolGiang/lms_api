package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CreateCommentRequest;
import com.example.lms_api.dto.request.CreatePostRequest;
import com.example.lms_api.dto.response.communit_response.CommentResponse;
import com.example.lms_api.dto.response.communit_response.CommunityActionResponse;
import com.example.lms_api.dto.response.communit_response.PostDetailResponse;
import com.example.lms_api.dto.response.communit_response.PostResponse;
import com.example.lms_api.entity.Comment;
import com.example.lms_api.entity.Post;
import com.example.lms_api.entity.UserEntity;
import com.example.lms_api.mapper.PostMapper;
import com.example.lms_api.repository.PostRepository;
import com.example.lms_api.repository.UserRepository;
import com.example.lms_api.service.PostService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<PostResponse> getPosts(String category, String query, int page, int size) {
        int pageIndex = Math.max(0, page - 1);
        int pageSize = Math.max(1, size);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageIndex, pageSize);

        String cat = (category == null) ? null : category.trim();
        String q = (query == null) ? null : query.trim();

        // Escape keyword để regex không bị ký tự đặc biệt phá
        String regex = null;
        if (q != null && !q.isEmpty()) {
            regex = ".*" + java.util.regex.Pattern.quote(q) + ".*";
        }

        org.springframework.data.domain.Page<Post> postsPage;

        if (regex != null) {
            if (cat != null && !cat.isEmpty()) {
                postsPage = postRepository.findByCategoryAndSearchQuery(cat, regex, pageable);
            } else {
                postsPage = postRepository.findBySearchQuery(regex, pageable);
            }
        } else {
            if (cat != null && !cat.isEmpty()) {
                postsPage = postRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(cat, pageable);
            } else {
                postsPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }

        // fallback authorRole cho data cũ (nếu bạn đã có enrichAuthorRole)
        List<Post> posts = postsPage.getContent();
        for (Post p : posts) enrichAuthorRole(p);

        return posts.stream().map(postMapper::toResponse).toList();
    }


    @Override
    public PostDetailResponse getPostDetail(String id, String currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id = " + id));

        post.setViews(post.getViews() + 1);
        post.setUpdatedAt(Instant.now());

        Post savedPost = postRepository.save(post);

        enrichAuthorRole(savedPost);

        return postMapper.toDetailResponse(savedPost, currentUserId);
    }

    @Override
    public PostResponse createPost(CreatePostRequest request, String userId) {
        validateCreatePostRequest(request);

        UserEntity user = findUserByIdString(userId);

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
        post.setAuthorName(user.getUsername());
        post.setAuthorRole(user.getRole().name()); // ✅

        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Override
    public CommunityActionResponse deletePost(String postId, String currentUserId, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id = " + postId));

        if (!isAdmin && (currentUserId == null || !currentUserId.equals(post.getUserId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa bài này");
        }

        postRepository.deleteById(postId);
        return CommunityActionResponse.deletePost(postId);
    }

    @Override
    public CommunityActionResponse toggleLike(String postId, String currentUserId) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id = " + postId));

        if (post.getLikes() == null) post.setLikes(new ArrayList<>());

        boolean liked;
        if (post.getLikes().contains(currentUserId)) {
            post.getLikes().remove(currentUserId);
            liked = false;
        } else {
            post.getLikes().add(currentUserId);
            liked = true;
        }

        post.setUpdatedAt(Instant.now());
        Post saved = postRepository.save(post);

        int likesCount = saved.getLikes() == null ? 0 : saved.getLikes().size();
        return CommunityActionResponse.like(postId, liked, likesCount);
    }

    @Override
    public CommentResponse addComment(String postId, CreateCommentRequest request, String currentUserId) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập nội dung bình luận");
        }

        UserEntity user = findUserByIdString(currentUserId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id = " + postId));

        if (post.getComments() == null) post.setComments(new ArrayList<>());

        String commentId = "cmt_" + UUID.randomUUID();
        Instant now = Instant.now();

        Comment comment = new Comment(
                commentId,
                currentUserId,
                user.getUsername(),
                request.getContent().trim(),
                now
        );

        post.getComments().add(comment);
        post.setUpdatedAt(now);
        postRepository.save(post);

        return new CommentResponse(commentId, currentUserId, user.getUsername(), comment.getContent(), now);
    }

    @Override
    public CommunityActionResponse deleteComment(String postId, String commentId, String currentUserId, boolean isAdmin) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id = " + postId));

        if (post.getComments() == null || post.getComments().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận");
        }

        Comment target = null;
        for (Comment c : post.getComments()) {
            if (commentId != null && commentId.equals(c.getCommentId())) {
                target = c;
                break;
            }
        }

        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận");
        }

        if (!isAdmin && !currentUserId.equals(target.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa bình luận này");
        }

        post.getComments().remove(target);
        post.setUpdatedAt(Instant.now());
        postRepository.save(post);

        return CommunityActionResponse.deleteComment(postId, commentId);
    }

    // ── Helpers ──────────────────────────────────────────────

    private void validateCreatePostRequest(CreatePostRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request không được null");
        if (request.getTitle() == null || request.getTitle().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tiêu đề không được để trống");
        if (request.getContent() == null || request.getContent().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nội dung không được để trống");
    }

    private UserEntity findUserByIdString(String userId) {
        try {
            Integer uid = Integer.parseInt(userId);
            return userRepository.findById(uid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found id=" + userId));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId: " + userId);
        }
    }

    // ✅ fallback authorRole cho post cũ chưa có field
    private void enrichAuthorRole(Post post) {
        if (post == null) return;
        if (post.getAuthorRole() != null && !post.getAuthorRole().isBlank()) return;

        String uidStr = post.getUserId();
        if (uidStr == null || uidStr.isBlank()) return;

        try {
            Integer uid = Integer.parseInt(uidStr);
            userRepository.findById(uid).ifPresent(u -> post.setAuthorRole(u.getRole().name()));
        } catch (NumberFormatException ignore) {
        }
    }
}
