package com.example.lms_api.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

@Document(collection = "course_reviews")
@CompoundIndexes({
    @CompoundIndex(name = "course_created_idx", def = "{'courseId': 1, 'createdAt': -1}")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    CourseReview {
    @Id
    private String id;
    private Integer courseId;
    private Integer studentId;
    private Integer enrollmentId;
    private Double rating;
    private ReviewDetails review;
    private HelpfulStats helpful;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewDetails {
        private String title;
        private String content;
        private List<String> pros = new ArrayList<>();
        private List<String> cons = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HelpfulStats {
        @Builder.Default
        private Integer upvotes = 0;
        @Builder.Default
        private Integer downvotes = 0;
        @Builder.Default
        private List<Integer> votedBy = new ArrayList<>();
        @Builder.Default
        private List<Integer> votedUpBy = new ArrayList<>();
        @Builder.Default
        private List<Integer> votedDownBy = new ArrayList<>();
    }
}