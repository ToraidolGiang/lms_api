package com.example.lms_api.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookRequest {
    private Integer paymentId;
    /** Pending | Paid | Failed | Refunded */
    private String paymentStatus;
}
