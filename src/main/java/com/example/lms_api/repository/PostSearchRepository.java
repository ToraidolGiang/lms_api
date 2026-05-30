package com.example.lms_api.repository;

import com.example.lms_api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearchRepository {
    // Chỉ định nghĩa tên hàm, không viết logic ở đây
    Page<Post> search(String query, String category, String sortBy, Pageable pageable);
}