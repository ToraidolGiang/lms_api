package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CreateCommentRequest;
import com.example.lms_api.dto.request.CreatePostRequest;
import com.example.lms_api.dto.response.communit_response.*;
import com.example.lms_api.entity.Comment;
import com.example.lms_api.entity.Post;
import com.example.lms_api.entity.User;
import com.example.lms_api.mapper.PostMapper;
import com.example.lms_api.repository.PostRepository;
import com.example.lms_api.repository.PostSearchRepository;
import com.example.lms_api.repository.UserRepository;
import com.example.lms_api.service.PostService;
import com.example.lms_api.service.NotificationService;
import com.example.lms_api.dto.request.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper, UserRepository userRepository, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Autowired
    private PostSearchRepository postSearchRepository;

    @Override
    public List<PostResponse> getPosts(String category, String query, String sortBy, int page, int size, String currentUserId) {
        int pageIndex = Math.max(0, page - 1);
        int pageSize = Math.max(1, size);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageIndex, pageSize);

        org.springframework.data.domain.Page<Post> postsPage = postSearchRepository.search(query, category, sortBy, pageable);

        List<Post> posts = postsPage.getContent();
        for (Post p : posts) enrichAuthorRole(p);

        return posts.stream().map(post -> postMapper.toResponse(post, currentUserId)).toList();
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
        User user = findUserByIdString(userId);
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
        post.setAuthorRole(user.getRole().name());



        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost, userId);
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"));

        if (post.getLikes() == null) {
            post.setLikes(new ArrayList<>());
        }

        boolean liked;
        if (post.getLikes().contains(currentUserId)) {
            post.getLikes().remove(currentUserId);
            liked = false;
        } else {
            post.getLikes().add(currentUserId);
            liked = true;
            
            // Gửi thông báo khi người dùng thả tim (nếu không phải tự thả tim bài của mình)
            try {
                if (!currentUserId.equals(post.getUserId())) {
                    User liker = findUserByIdString(currentUserId);
                    NotificationRequest notifReq = new NotificationRequest();
                    notifReq.setTargetUserId(Integer.parseInt(post.getUserId()));
                    notifReq.setTitle("Có người thích bài viết của bạn");
                    notifReq.setMessage(liker.getUsername() + " đã thích bài viết: " + post.getTitle());
                    notifReq.setLink("community/" + postId);
                    notifReq.setType("LIKE_POST");
                    notificationService.createNotification(notifReq);
                }
            } catch (Exception e) {
                System.err.println("Lỗi gửi thông báo thả tim: " + e.getMessage());
            }
        }

        postRepository.save(post);
        int likesCount = post.getLikes().size();
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

        User user = findUserByIdString(currentUserId);
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
                request.getParentCommentId(),
                now
        );

        post.getComments().add(comment);
        post.setUpdatedAt(now);
        postRepository.save(post);

        try {
            if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
                Comment parentComment = null;
                for (Comment c : post.getComments()) {
                    if (c.getCommentId().equals(request.getParentCommentId())) {
                        parentComment = c;
                        break;
                    }
                }
                if (parentComment != null && !parentComment.getUserId().equals(currentUserId)) {
                    NotificationRequest notifReq = new NotificationRequest();
                    notifReq.setTargetUserId(Integer.parseInt(parentComment.getUserId()));
                    notifReq.setTitle("Có người trả lời bình luận của bạn");
                    notifReq.setMessage(user.getUsername() + " đã trả lời bình luận của bạn trong bài viết: " + post.getTitle());
                    notifReq.setLink("community/" + postId); 
                    notifReq.setType("REPLY_COMMENT");
                    notificationService.createNotification(notifReq);
                }
            } else {
                if (!post.getUserId().equals(currentUserId)) {
                    NotificationRequest notifReq = new NotificationRequest();
                    notifReq.setTargetUserId(Integer.parseInt(post.getUserId()));
                    notifReq.setTitle("Bình luận mới trong bài viết của bạn");
                    notifReq.setMessage(user.getUsername() + " đã bình luận về bài viết: " + post.getTitle());
                    notifReq.setLink("community/" + postId);
                    notifReq.setType("NEW_COMMENT");
                    notificationService.createNotification(notifReq);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo: " + e.getMessage());
        }

        return new CommentResponse(commentId, currentUserId, user.getUsername(), comment.getContent(), comment.getParentCommentId(), now);
    }

    // 🌟 BỔ SUNG LOGIC THỰC THI: Chỉnh sửa bình luận bài viết
    @Override
    public CommentResponse updateComment(String postId, String commentId, CreateCommentRequest request, String currentUserId) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập nội dung bình luận cần chỉnh sửa");
        }

        // 1. Tìm kiếm bài viết chứa bình luận
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id = " + postId));

        if (post.getComments() == null || post.getComments().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bài viết chưa có bất kỳ bình luận nào");
        }

        // 2. Tìm bình luận mục tiêu trong mảng lồng
        Comment targetComment = null;
        for (Comment c : post.getComments()) {
            if (commentId != null && commentId.equals(c.getCommentId())) {
                targetComment = c;
                break;
            }
        }

        if (targetComment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận id = " + commentId);
        }

        // 3. KIỂM TRA BẢO MẬT CHÍ MANH: Chỉ chính chủ viết ra bình luận mới có quyền sửa đổi
        if (!currentUserId.equals(targetComment.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa bình luận của người khác");
        }

        // 4. Tiến hành đè nội dung mới và lưu lại
        Instant now = Instant.now();
        targetComment.setContent(request.getContent().trim());
        targetComment.setCreatedAt(now); // Cập nhật mốc thời gian sửa đổi mới nhất

        post.setUpdatedAt(now);
        postRepository.save(post);

        return new CommentResponse(
                targetComment.getCommentId(),
                targetComment.getUserId(),
                targetComment.getAuthorName(),
                targetComment.getContent(),
                targetComment.getParentCommentId(),
                targetComment.getCreatedAt()
        );
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

    @Override
    public PostResponse updatePost(String postId, com.example.lms_api.dto.request.UpdatePostRequest request, String currentUserId, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết id = " + postId));

        if (currentUserId == null || !currentUserId.equals(post.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa bài viết này");
        }

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            post.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            post.setContent(request.getContent().trim());
        }
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            post.setCategory(request.getCategory().trim());
        }
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            post.setType(request.getType().trim());
        }
        if (request.getTags() != null) {
            post.setTags(request.getTags());
        }

        post.setUpdatedAt(Instant.now());
        Post updatedPost = postRepository.save(post);
        return postMapper.toResponse(updatedPost, currentUserId);
    }

    @Override
    public CommunityStatsResponse getCommunityStats() {
        long totalMembers = userRepository.count();
        long totalTopics = postRepository.count();
        long totalReplies = 0;

        List<Post> allPosts = postRepository.findAll();
        for (Post p : allPosts) {
            if (p.getComments() != null) {
                totalReplies += p.getComments().size();
            }
        }

        Instant tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES);
        Set<String> activeUserIds = new HashSet<>();

        for (Post p : allPosts) {
            if (p.getCreatedAt() != null && p.getCreatedAt().isAfter(tenMinutesAgo)) {
                if (p.getUserId() != null) {
                    activeUserIds.add(p.getUserId());
                }
            }

            if (p.getComments() != null) {
                for (Comment c : p.getComments()) {
                    if (c.getCreatedAt() != null && c.getCreatedAt().isAfter(tenMinutesAgo)) {
                        if (c.getUserId() != null) {
                            activeUserIds.add(c.getUserId());
                        }
                    }
                }
            }
        }

        int liveOnlineCount = activeUserIds.size();
        if (liveOnlineCount == 0) {
            liveOnlineCount = 1;
        }

        return new CommunityStatsResponse(totalMembers, totalTopics, totalReplies, liveOnlineCount);
    }



    private void validateCreatePostRequest(CreatePostRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request không được null");
        if (request.getTitle() == null || request.getTitle().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tiêu đề không được để trống");
        if (request.getContent() == null || request.getContent().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nội dung không được để trống");
    }

    private User findUserByIdString(String userId) {
        try {
            Integer uid = Integer.parseInt(userId);
            return userRepository.findById(uid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found id=" + userId));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId: " + userId);
        }
    }

    private void enrichAuthorRole(Post post) {
        if (post == null) return;
        if (post.getAuthorRole() != null && !post.getAuthorRole().isBlank()) return;

        String uidStr = post.getUserId();
        if (uidStr == null || uidStr.isBlank()) return;

        try {
            Integer uid = Integer.parseInt(uidStr);
            userRepository.findById(uid).ifPresent(u -> {
                if (u.getRole() != null) {
                    post.setAuthorRole(u.getRole().name());
                } else {
                    post.setAuthorRole("TEACHER");
                }
            });
        } catch (NumberFormatException ignore) {
        }
    }
}