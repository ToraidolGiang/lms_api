package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.entity.Category;
import com.example.lms_api.entity.Course;
import com.example.lms_api.mapper.CourseMapper;
import com.example.lms_api.repository.CategoryRepository;
import com.example.lms_api.repository.CourseRepository;
import com.example.lms_api.repository.EnrollmentRepository;
import com.example.lms_api.repository.StudentRepository;
import com.example.lms_api.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;             // ← inject mapper

    // ── Tạo mới ──────────────────────────────────────────────
    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại!"));

        // Dùng mapper tạo entity, rồi set các field đặc biệt thủ công
        Course course = courseMapper.toEntity(request);
        course.setCategory(category);
        course.setCreatedAt(LocalDateTime.now());
        course.setIsActive(true);
        course.setIsDeleted(false);
        course.setArchiveStatus("Active");
        // course.setTeacher(teacherRepository.findById(request.getTeacherId()).orElse(null));

        return courseMapper.toResponse(courseRepository.save(course));
    }

    // ── Lấy tất cả (chưa xoá, đang active) ──────────────────
    @Override
    public List<CourseResponse> getAllActiveCourses() {
        return courseRepository.findByIsDeletedFalseAndIsActiveTrue()
                .stream()
                .map(courseMapper::toResponse)     // ← dùng mapper
                .collect(Collectors.toList());
    }

    // ── Lấy theo ID ──────────────────────────────────────────
    @Override
    public CourseResponse getCourseById(Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));

        if (Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("Khóa học này đã bị xóa!");
        }

        CourseResponse response = courseMapper.toResponse(course);

        // Đính kèm trạng thái đã mua cho user hiện tại (nếu có đăng nhập)
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                Integer userId = Integer.parseInt(auth.getName());
                studentRepository.findByUser_Id(userId).ifPresent(student -> {
                    boolean purchased = enrollmentRepository.existsByCourse_CourseIdAndStudent_StudentIdAndAccessStatus(
                            course.getCourseId(), student.getStudentId(), "Active");
                    response.setPurchased(purchased);
                    if (purchased) {
                        response.setAccessStatus("Active");
                    }
                });
            }
        } catch (Exception ignored) {
            // Không làm fail API nếu không lấy được user/student
        }

        if (response.getPurchased() == null) response.setPurchased(false);
        if (response.getAccessStatus() == null) response.setAccessStatus("Pending");

        return response;
    }

    // ── Cập nhật ─────────────────────────────────────────────
    @Override
    @Transactional
    public CourseResponse updateCourse(Integer id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học để cập nhật"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại!"));

        // Dùng mapper đè lên entity cũ, rồi set riêng các quan hệ
        courseMapper.updateEntityFromRequest(request, course);
        course.setCategory(category);
        // course.setTeacher(teacherRepository.findById(request.getTeacherId()).orElse(null));

        return courseMapper.toResponse(courseRepository.save(course));
    }

    // ── Xoá mềm (Soft Delete) ────────────────────────────────
    @Override
    @Transactional
    public void deleteCourse(Integer id, String deletedBy, String reason) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học để xóa"));

        course.setIsDeleted(true);
        course.setIsActive(false);
        course.setDeletedAt(LocalDateTime.now());
        course.setDeletedBy(deletedBy);
        course.setDeleteReason(reason);
        course.setArchiveStatus("Deleted");

        courseRepository.save(course);
    }
}