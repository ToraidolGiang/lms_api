package com.example.lms_api.mapper;

import com.example.lms_api.dto.request.CategoryRequest;
import com.example.lms_api.dto.response.CategoryResponse;
import com.example.lms_api.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    // 1. Entity → Response
    CategoryResponse toResponse(Category entity);

    // 2. Request → Entity (tạo mới)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "courses", ignore = true)
    Category toEntity(CategoryRequest request);

    // 3. Request đè lên Entity cũ (cập nhật)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category entity);
}