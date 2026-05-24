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

    /**
     * Chuỗi text để client tự render QR.
     * (Có thể thay bằng VietQR/EMVCo string trong tương lai)
     */
    private String qrText;
}
