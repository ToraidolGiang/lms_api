package com.example.lms_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    // Spring Boot sẽ tự động lấy giá trị từ application.properties đắp vào đây
    @Value("${PAYOS_CLIENT_ID}")
    private String clientId;

    @Value("${PAYOS_API_KEY}")
    private String apiKey;

    @Value("${PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        String clientId = "12751b06-b7c4-4c5a-9e57-d33e68cdc923"; // Copy lại mã thật trên web dán vào đây
        String apiKey = "4dad4e92-f8c1-4bf0-b59f-5c3ea8a37375"; // Copy lại mã thật dán vào đây
        String checksumKey = "5c0d564b86e464ba1edcb1d967b34458c561cec362f99642133b37d008521418"; // Lấy ĐÚNG mã Checksum của "Kênh thanh toán"

        return new PayOS(clientId, apiKey, checksumKey);
    }
}