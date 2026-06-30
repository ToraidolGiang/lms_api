package com.example.lms_api.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    // Spring sẽ tự động tìm biến môi trường có tên MONGO_URI trong file .env hoặc hệ thống
    @Value("${MONGO_URI}")
    private String mongoUri;

    // Spring sẽ tự động tìm biến môi trường có tên MONGO_DATABASE
    @Value("${MONGO_DATABASE}")
    private String mongoDatabase;

    @Bean
    public MongoClient mongoClient() {
        // Không còn ép cứng nữa, truyền biến mongoUri vào đây
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        // Truyền biến mongoDatabase vào đây
        return new MongoTemplate(mongoClient, mongoDatabase);
    }
}
