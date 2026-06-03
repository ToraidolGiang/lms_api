// entity/Submission.java
package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Submission {
    @Id
    @Column(name = "submissionid", length = 50)
    private String submissionId;

    // Lưu lessonId (ví dụ: M001_L002) vào đây để map với nội dung
    @Column(name = "aqid", length = 50)
    private String aqId;

    // Trong DB của bạn StudentID là VARCHAR(50), nên ta ép kiểu ID Int sang String
    @Column(name = "studentid", length = 50)
    private String studentId;

    @Column(name = "submittedat")
    private LocalDateTime submittedAt;

    @Column(name = "fileurl", length = 500)
    private String fileUrl;

    @Column(name = "answers", columnDefinition = "TEXT")
    private String answers;

    @Column(name = "attemptcount")
    private Integer attemptCount;
}