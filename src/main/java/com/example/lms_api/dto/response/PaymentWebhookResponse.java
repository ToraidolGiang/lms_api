package com.example.lms_api.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookResponse {
    private Integer paymentId;
    private String paymentStatus;
    private Integer enrollmentId;
}
