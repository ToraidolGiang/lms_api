package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CategoryRequest;
import com.example.lms_api.dto.response.CategoryResponse;
import com.example.lms_api.entity.Category;
import com.example.lms_api.mapper.CategoryMapper;
import com.example.lms_api.repository.CategoryRepository;
import com.example.lms_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;          // ← inject mapper

    // ── Tạo mới ──────────────────────────────────────────────
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByCategoryName(request.getCategoryName()).isPresent()) {
            throw new RuntimeException("Tên danh mục này đã tồn tại!");
        }

        // Dùng mapper thay vì set thủ công
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    // ── Lấy tất cả ───────────────────────────────────────────
    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)   // ← dùng mapper
                .collect(Collectors.toList());
    }

    // ── Lấy theo ID ──────────────────────────────────────────
    @Override
    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        return categoryMapper.toResponse(category); // ← dùng mapper
    }

    // ── Cập nhật ─────────────────────────────────────────────
    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục để cập nhật"));

        // Dùng mapper đè lên entity cũ
        categoryMapper.updateEntityFromRequest(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    // ── Xoá ──────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục để xóa"));

        if (category.getCourses() != null && !category.getCourses().isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục này vì đang có khóa học thuộc danh mục!");
        }

        categoryRepository.delete(category);
    }
}