package com.example.lms_api.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmitGradeRequest {
    private Integer studentId;   // bắt buộc
    private Integer courseId;    // bắt buộc
    private BigDecimal examScore; // điểm giáo viên nhập (0–10 hoặc 0–100 tuỳ bạn)
    private Boolean isMasked;    // optional, default false
}
