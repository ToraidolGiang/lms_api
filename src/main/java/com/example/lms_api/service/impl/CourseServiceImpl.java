package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.entity.CategoryEntity;
import com.example.lms_api.entity.CourseEntity;
import com.example.lms_api.repository.CategoryRepository;
import com.example.lms_api.repository.CourseRepository;
import com.example.lms_api.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    // Inject thêm TeacherRepository vào đây nếu có nhé bạn

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại!"));

        CourseEntity course = CourseEntity.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .category(category)
                // .teacher(teacherRepository.findById(request.getTeacherId()).orElse(null))
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .isDeleted(false)
                .archiveStatus("Active")
                .build();

        CourseEntity savedCourse = courseRepository.save(course);
        return mapToResponse(savedCourse);
    }

    @Override
    public List<CourseResponse> getAllActiveCourses() {
        return courseRepository.findByIsDeletedFalseAndIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseById(Integer id) {
        CourseEntity course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));
        if (Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("Khóa học này đã bị xóa!");
        }
        return mapToResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Integer id, CourseRequest request) {
        CourseEntity course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học để cập nhật"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại!"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setImageUrl(request.getImageUrl());
        course.setPrice(request.getPrice());
        course.setCategory(category);

        return mapToResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void deleteCourse(Integer id, String deletedBy, String reason) {
        CourseEntity course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học để xóa"));

        // Thực hiện Soft Delete theo đúng thiết kế DB của bạn
        course.setIsDeleted(true);
        course.setIsActive(false);
        course.setDeletedAt(LocalDateTime.now());
        course.setDeletedBy(deletedBy);
        course.setDeleteReason(reason);
        course.setArchiveStatus("Deleted");

        courseRepository.save(course);
    }

    // Hàm phụ trợ để map từ Entity sang DTO nhanh gọn
    private CourseResponse mapToResponse(CourseEntity entity) {
        return CourseResponse.builder()
                .courseId(entity.getCourseId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .archiveStatus(entity.getArchiveStatus())
                .categoryName(entity.getCategory() != null ? entity.getCategory().getCategoryName() : null)
                // .teacherName(entity.getTeacher() != null ? entity.getTeacher().getName() : null)
                .build();
    }
}