package com.example.lms_api.repository;

import com.example.lms_api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PostRepository extends MongoRepository<Post, String> {

    // List thường (paging)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category, Pageable pageable);

    // Search theo title OR content (regex, case-insensitive)
    @Query("{ '$or': [ " +
            "{ 'title':   { '$regex': ?0, '$options': 'i' } }, " +
            "{ 'content': { '$regex': ?0, '$options': 'i' } } " +
            "] }")
    Page<Post> findBySearchQuery(String regex, Pageable pageable);

    // Search kết hợp category + (title OR content)
    @Query("{ 'category': ?0, '$or': [ " +
            "{ 'title':   { '$regex': ?1, '$options': 'i' } }, " +
            "{ 'content': { '$regex': ?1, '$options': 'i' } } " +
            "] }")
    Page<Post> findByCategoryAndSearchQuery(String category, String regex, Pageable pageable);
}
