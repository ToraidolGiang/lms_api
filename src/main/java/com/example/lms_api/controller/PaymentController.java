package com.example.lms_api.controller;

import com.example.lms_api.dto.request.PaymentCheckoutRequest;
import com.example.lms_api.dto.request.PayOSWebhookRequest;
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

    @PostMapping("/webhook")
    public ResponseEntity<PaymentWebhookResponse> webhook(@RequestBody PayOSWebhookRequest request) {
        return ResponseEntity.ok(paymentService.handleWebhook(request));
    }
}