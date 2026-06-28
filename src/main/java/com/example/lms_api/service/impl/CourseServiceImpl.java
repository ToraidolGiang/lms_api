package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.PagedResponse;
import com.example.lms_api.entity.Category;
import com.example.lms_api.entity.Course;
import com.example.lms_api.entity.Teacher;
import com.example.lms_api.mapper.CourseMapper;
import com.example.lms_api.projection.CourseSummaryProjection;
import com.example.lms_api.repository.CategoryRepository;
import com.example.lms_api.repository.CourseRepository;
import com.example.lms_api.repository.CourseReviewRepository;
import com.example.lms_api.repository.TeacherRepository;
import com.example.lms_api.service.CourseService;
import com.example.lms_api.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lms_api.service.CourseContentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper courseMapper;             // ← inject mapper
    private final CourseContentService courseContentService;
    private final CourseReviewServiceImpl courseReviewService;
    private final SecurityUtil securityUtil;


    // ── Tạo mới ──────────────────────────────────────────────
    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại!"));

        Teacher teacher = teacherRepository.findByTeacherId(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher không tồn tại!"));

        // Dùng mapper tạo entity, rồi set các field đặc biệt thủ công
        Course course = courseMapper.toEntity(request);
        course.setCategory(category);
        course.setTeacher(teacher);
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

        Teacher teacher = teacherRepository.findByTeacherId(course.getTeacher().getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giang vien với ID: " + id));
        CourseResponse c = new CourseResponse();
        c = courseMapper.toResponse(course);
        c.setTeacherName(teacher.getFirstName()+ " "+teacher.getLastName());

        return c;    // ← dùng mapper
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

    // ── Lấy tất cả khóa học theo teacherId ──────────────────
    @Override
    public List<CourseResponse> getCourseByTeacherId(Integer teacherId) {
        return courseRepository.findByTeacherTeacherId(teacherId)
                .stream()
                .map(courseMapper::toResponse)     // ← dùng mapper
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getExploreCourses() {
        // 1. Gọi query SQL để lấy thông tin tổng hợp
        List<CourseSummaryProjection> projections = courseRepository.getExploreCourses();

        // 2. Chuyển đổi từ Projection sang Response DTO
        return projections.stream().map(p -> {
            CourseResponse response = new CourseResponse();
            response.setCourseId(p.getCourseId());
            response.setTitle(p.getCourseTitle());
            response.setPrice(p.getPrice());
            response.setImageUrl(p.getImageUrl());
            response.setTeacherName(p.getTeacherName());
            response.setCategoryName(p.getCategoryName());
            response.setTotalStudents(p.getTotalStudents());
            Integer totalLessons = courseContentService.getTotalLessons(p.getCourseId());
            response.setTotalLessons(totalLessons);
            Double rating = courseReviewService.getAverageRating(p.getCourseId());
            response.setAverageRating(rating);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getExploreCoursesTea() {

        Integer teacherId = securityUtil.getCurrentTeacherId();
        // 1. Gọi query SQL để lấy thông tin tổng hợp
        List<CourseSummaryProjection> projections = courseRepository.getExploreCourses(teacherId);

        // 2. Chuyển đổi từ Projection sang Response DTO
        return projections.stream().map(p -> {
            CourseResponse response = new CourseResponse();
            response.setCourseId(p.getCourseId());
            response.setTitle(p.getCourseTitle());
            response.setPrice(p.getPrice());
            response.setImageUrl(p.getImageUrl());
            response.setTeacherName(p.getTeacherName());
            response.setCategoryName(p.getCategoryName());
            response.setTotalStudents(p.getTotalStudents());
            Integer totalLessons = courseContentService.getTotalLessons(p.getCourseId());
            response.setTotalLessons(totalLessons);
            Double rating = courseReviewService.getAverageRating(p.getCourseId());
            response.setAverageRating(rating);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public PagedResponse<CourseResponse> getExploreCoursesPagedStudent(int page, int size) {
        List<CourseResponse> all = getExploreCourses();
        return PagedResponse.of(all, page, size);
    }

    @Override
    public PagedResponse<CourseResponse> getExploreCoursesPagedTeacher(int page, int size) {
        List<CourseResponse> all = getExploreCoursesTea();
        return PagedResponse.of(all, page, size);
    }
}