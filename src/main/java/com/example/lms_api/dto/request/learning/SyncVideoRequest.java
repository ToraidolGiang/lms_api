package com.example.lms_api.dto.request.learning;

import lombok.Data;

@Data
public class SyncVideoRequest {
    private Integer currentSeconds;
    private Integer totalSeconds;
}