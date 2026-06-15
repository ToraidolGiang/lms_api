package com.example.lms_api.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCheckoutResponse {
    private Integer paymentId;
    private BigDecimal amount;
    private String paymentStatus;

    private String qrText; // Có thể giữ lại hoặc xóa đi nếu không dùng nữa

    // THÊM TRƯỜNG NÀY VÀO
    private String checkoutUrl;
}