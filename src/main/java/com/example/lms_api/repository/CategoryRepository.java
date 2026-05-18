package com.example.lms_api.repository;


import com.example.lms_api.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    // Tìm danh mục theo tên (nếu cần)
    Optional<CategoryEntity> findByCategoryName(String categoryName);
}