package com.example.lms_api.repository;

import com.example.lms_api.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // Spring Data MongoDB tự động hiểu và chuyển thành query tìm theo userId
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
}