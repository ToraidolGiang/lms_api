package com.example.lms_api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCommentRequest {
    private String content;
    private String parentCommentId;
}
