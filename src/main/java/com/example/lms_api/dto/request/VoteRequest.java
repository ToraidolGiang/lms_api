package com.example.lms_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {
    @NotNull(message = "ID học viên không được để trống")
    private Integer studentId;

    @NotNull(message = "Loại vote không được để trống (true = upvote, false = downvote)")
    private Boolean isUpvote;
}