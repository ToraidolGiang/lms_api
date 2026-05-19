package com.example.lms_api.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        // Ép cứng kết nối thẳng vào localhost (Bỏ qua mọi file properties)
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        // Tuyên bố quyền lực tối cao: ÉP CỨNG tên database là lms_mongo_db
        return new MongoTemplate(mongoClient, "LMS");
    }
}