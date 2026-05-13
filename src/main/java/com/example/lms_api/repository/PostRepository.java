package com.example.lms_api.repository;

import com.example.lms_api.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    List<Post> findByTypeIgnoreCaseOrderByCreatedAtDesc(String type);
}