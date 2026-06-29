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

        // 1. Kiểm tra trùng tên khóa học
        if (courseRepository.existsByTitleAndTeacher_TeacherId(request.getTitle(), request.getTeacherId())) {
            throw new IllegalArgumentException("Bạn đã có một khóa học với tên này rồi");
        }

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
    public PagedResponse<CourseResponse> getExploreCoursesPagedStudent(int page, int size, String search, String category, String price, String rating) {
        List<CourseResponse> all = getExploreCourses();
        List<CourseResponse> filtered = filterCourses(all, search, category, price, rating);
        return PagedResponse.of(filtered, page, size);
    }

    @Override
    public PagedResponse<CourseResponse> getExploreCoursesPagedTeacher(int page, int size, String search, String category, String price, String rating) {
        List<CourseResponse> all = getExploreCoursesTea();
        List<CourseResponse> filtered = filterCourses(all, search, category, price, rating);
        return PagedResponse.of(filtered, page, size);
    }

    private List<CourseResponse> filterCourses(List<CourseResponse> all, String search, String category, String price, String ratingStr) {
        return all.stream()
                .filter(c -> {
                    // 1. Search
                    if (search != null && !search.trim().isEmpty()) {
                        if (c.getTitle() == null || !c.getTitle().toLowerCase().contains(search.toLowerCase().trim())) {
                            return false;
                        }
                    }
                    // 2. Category
                    if (category != null && !category.equalsIgnoreCase("All") && !category.trim().isEmpty()) {
                        if (c.getCategoryName() == null || !c.getCategoryName().equalsIgnoreCase(category)) {
                            return false;
                        }
                    }
                    // 3. Price
                    if (price != null && !price.equalsIgnoreCase("All") && !price.trim().isEmpty()) {
                        double itemPrice = (c.getPrice() != null) ? c.getPrice().doubleValue() : 0.0;
                        if (price.equalsIgnoreCase("Free")) {
                            if (itemPrice > 0) return false;
                        } else if (price.equalsIgnoreCase("Under $50")) {
                            if (itemPrice >= 50) return false;
                        } else if (price.equalsIgnoreCase("$50-$100")) {
                            if (itemPrice < 50 || itemPrice > 100) return false;
                        } else if (price.equalsIgnoreCase("Over $100")) {
                            if (itemPrice <= 100) return false;
                        }
                    }
                    // 4. Rating
                    if (ratingStr != null && !ratingStr.equalsIgnoreCase("Any") && !ratingStr.trim().isEmpty()) {
                        try {
                            String r = ratingStr.replaceAll("[^0-9.]", "");
                            if (!r.isEmpty()) {
                                double targetRate = Double.parseDouble(r);
                                double itemRate = c.getAverageRating();
                                if (itemRate < targetRate) return false;
                            }
                        } catch (Exception ignored) {}
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}