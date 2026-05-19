package com.example.lms_api.dto.response.communit_response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostDetailResponse extends PostResponse {

    private boolean likedByMe;

    private List<CommentResponse> comments;

    public PostDetailResponse() {
        super();
    }
}
