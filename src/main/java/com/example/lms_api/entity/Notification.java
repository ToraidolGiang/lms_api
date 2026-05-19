package com.example.lms_api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

// Bỏ @Entity và @Table của JPA đi, dùng @Document của MongoDB
@Document(collection = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification { // Nên đổi tên thành NotificationDocument

    @Id // Chú ý: import org.springframework.data.annotation.Id; (Không phải của jakarta)
    private String id;

    // Lưu thẳng ID của User, KHÔNG dùng @ManyToOne hay UserEntity nữa
    @Field("userId")
    private String userId;

    private String title;
    private String message; // Đổi từ body -> message cho khớp với JSON của bạn
    private String type;
    private String link;    // Thêm trường link theo JSON của bạn

    @JsonProperty("isRead")
    @Builder.Default
    private boolean read = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}