package com.example.lms_api.entity.content;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
// ── Base class cho Content (đa hình) ─────────────────────────
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentContent extends LessonContent {
    private String instruction;
    private String attachmentUrl;
}