package com.example.lms_api.controller;

import com.example.lms_api.dto.request.PaymentCheckoutRequest;
import com.example.lms_api.dto.request.PaymentWebhookRequest;
import com.example.lms_api.dto.response.PaymentCheckoutResponse;
import com.example.lms_api.dto.response.PaymentWebhookResponse;
import com.example.lms_api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<PaymentCheckoutResponse> checkout(@RequestBody PaymentCheckoutRequest request) {
        return ResponseEntity.ok(paymentService.checkout(request));
    }

    /**
     * Endpoint xác nhận thanh toán (demo webhook).
     * Thực tế bạn sẽ xác minh chữ ký/nguồn gửi từ bank/payment gateway.
     */
    @PostMapping("/webhook")
    public ResponseEntity<PaymentWebhookResponse> webhook(@RequestBody PaymentWebhookRequest request) {
        return ResponseEntity.ok(paymentService.handleWebhook(request));
    }
}
