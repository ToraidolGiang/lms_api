package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CategoryRequest;
import com.example.lms_api.dto.response.CategoryResponse;
import com.example.lms_api.entity.CategoryEntity;
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

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Kiểm tra xem tên danh mục đã tồn tại chưa (Tùy chọn)
        if (categoryRepository.findByCategoryName(request.getCategoryName()).isPresent()) {
            throw new RuntimeException("Tên danh mục này đã tồn tại!");
        }

        CategoryEntity category = CategoryEntity.builder()
                .categoryName(request.getCategoryName())
                .build();

        CategoryEntity savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Integer id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục để cập nhật"));

        category.setCategoryName(request.getCategoryName());

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục để xóa"));

        // Lưu ý: Nếu danh mục này đang có Khóa học (Courses) tham chiếu tới,
        // việc xóa cứng (hard delete) có thể gây lỗi DataIntegrityViolationException.
        // Bạn có thể check trước nếu category.getCourses().isEmpty() == false thì chặn không cho xóa.
        if (category.getCourses() != null && !category.getCourses().isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục này vì đang có khóa học thuộc danh mục!");
        }

        categoryRepository.delete(category);
    }

    // Hàm phụ trợ map Entity sang DTO
    private CategoryResponse mapToResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .build();
    }
}