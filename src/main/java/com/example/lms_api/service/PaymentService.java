package com.example.lms_api.service;

import com.example.lms_api.dto.request.PaymentCheckoutRequest;
import com.example.lms_api.dto.request.PaymentWebhookRequest;
import com.example.lms_api.dto.response.PaymentCheckoutResponse;
import com.example.lms_api.dto.response.PaymentWebhookResponse;

public interface PaymentService {
    //yêu cầu thông tin thanh toán, QE
    PaymentCheckoutResponse checkout(PaymentCheckoutRequest request);
    //gửi đã thanh toán
    PaymentWebhookResponse handleWebhook(PaymentWebhookRequest request);
}
