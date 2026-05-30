package com.example.lms_api.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReviewResponse {
    private String id;
    private Integer courseId;
    private Integer studentId;
    private String studentName; // Sẽ bổ sung từ việc map DB Postgres qua
    private Double rating;
    private String title;
    private String content;
    private List<String> pros;
    private List<String> cons;
    private Integer upvotes;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}