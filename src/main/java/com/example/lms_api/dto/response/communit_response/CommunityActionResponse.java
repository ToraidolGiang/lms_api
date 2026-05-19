package com.example.lms_api.dto.response.communit_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityActionResponse {
    public boolean success;
    public boolean deleted;
    public boolean liked;
    public String message;
    public String id;        // FE có field này
    public String postId;
    public String commentId;
    public int likesCount;

    public static CommunityActionResponse like(String postId, boolean liked, int likesCount) {
        return new CommunityActionResponse(true, false, liked, "OK", postId, postId, null, likesCount);
    }

    public static CommunityActionResponse deletePost(String postId) {
        return new CommunityActionResponse(true, true, false, "Deleted", postId, postId, null, 0);
    }

    public static CommunityActionResponse deleteComment(String postId, String commentId) {
        return new CommunityActionResponse(true, true, false, "Deleted", postId, postId, commentId, 0);
    }
}
