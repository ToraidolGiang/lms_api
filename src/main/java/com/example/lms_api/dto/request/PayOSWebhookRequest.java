package com.example.lms_api.dto.request;

import lombok.Data;

@Data
public class PayOSWebhookRequest {
    private String code;
    private String desc;
    private boolean success;
    private PayOSWebhookData data;
    private String signature;

    @Data
    public static class PayOSWebhookData {
        private Integer orderCode; // Trường này tương ứng với ID đơn thanh toán (paymentId) của bạn
        private Integer amount;
        private String description;
        private String accountNumber;
        private String reference;
        private String transactionDateTime;
        private String currency;
        private String paymentLinkId;
        private String code;
        private String desc;
    }
}