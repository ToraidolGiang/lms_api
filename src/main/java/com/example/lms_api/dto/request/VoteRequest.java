package com.example.lms_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {
    @NotNull(message = "ID học viên không được để trống")
    private Integer studentId;

    @JsonProperty("isUpvote")
    @NotNull(message = "Loại vote không được để trống (true = upvote, false = downvote)")
    private Boolean isUpvote;
}