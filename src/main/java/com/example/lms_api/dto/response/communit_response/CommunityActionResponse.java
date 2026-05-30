package com.example.lms_api.dto.response.communit_response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityActionResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("deleted")
    private boolean deleted;

    @JsonProperty("liked")
    private boolean liked;

    @JsonProperty("message")
    private String message;

    @JsonProperty("id")
    private String id;

    @JsonProperty("postId")
    private String postId;

    @JsonProperty("commentId")
    private String commentId;

    @JsonProperty("likesCount")
    private int likesCount;

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