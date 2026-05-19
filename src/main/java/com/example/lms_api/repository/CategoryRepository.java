package com.example.lms_api.repository;


import com.example.lms_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Tìm danh mục theo tên (nếu cần)
    Optional<Category> findByCategoryName(String categoryName);
}