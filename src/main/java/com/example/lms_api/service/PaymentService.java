package com.example.lms_api.service;

import com.example.lms_api.dto.request.PaymentCheckoutRequest;
import com.example.lms_api.dto.request.PayOSWebhookRequest;
import com.example.lms_api.dto.response.PaymentCheckoutResponse;
import com.example.lms_api.dto.response.PaymentWebhookResponse;

public interface PaymentService {
    PaymentCheckoutResponse checkout(PaymentCheckoutRequest request);
    PaymentWebhookResponse handleWebhook(PayOSWebhookRequest request);
}