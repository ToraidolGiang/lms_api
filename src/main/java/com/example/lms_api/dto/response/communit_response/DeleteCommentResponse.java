package com.example.lms_api.dto.response.communit_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCommentResponse {
    private boolean deleted;
    private String postId;
    private String commentId;
}
