package com.example.lms_api.repository;

import com.example.lms_api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearchRepository {
    Page<Post> search(String q, String category, Pageable pageable);
}
