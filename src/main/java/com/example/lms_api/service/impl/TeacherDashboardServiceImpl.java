package com.example.lms_api.service.impl;

import com.example.lms_api.dto.response.CourseResponse;
import com.example.lms_api.dto.response.TeacherDashboardResponse;
import com.example.lms_api.entity.Course;
import com.example.lms_api.repository.CourseRepository;
import com.example.lms_api.repository.EnrollmentRepository;
import com.example.lms_api.service.TeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherDashboardServiceImpl implements TeacherDashboardService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public TeacherDashboardResponse getDashboard(Integer userId) {
        // 1. Lấy số liệu từ DB
        long totalStudents = enrollmentRepository.countDistinctStudentsByTeacherId(userId);
        long enrollmentsThisMonth = enrollmentRepository.countEnrollmentsThisMonth(userId);

        Double rawRevenue = enrollmentRepository.sumRevenueByTeacherId(userId);
        BigDecimal totalRevenue = rawRevenue != null ? BigDecimal.valueOf(rawRevenue) : BigDecimal.ZERO;

        long totalCourses = courseRepository.countByTeacher_TeacherId(userId);

        // TODO: Móc từ ReviewRepository sau
        double avgRating = 4.8;

        // 2. Lấy danh sách khóa học
        List<CourseResponse> courseResponses = getMyCourses(userId);

        // 3. Build dữ liệu trả về (Kết hợp số thật và text giả định cho UI)
        return TeacherDashboardResponse.builder()
                .totalStudents(totalStudents)
                .enrollmentsThisMonth(enrollmentsThisMonth)
                .totalRevenue(totalRevenue)
                .avgRating(avgRating)
                .totalCourses(totalCourses)

                // Các trường hiển thị thêm cho đẹp giao diện
                .earningsChange("+18% from last month")
                .studentsChange("+" + enrollmentsThisMonth + " ↑")
                .revenueChange("+15% ↑")
                .ratingChange("+0.2 ↑")

                .courses(courseResponses)
                .build();
    }

    @Override
    public List<CourseResponse> getMyCourses(Integer userId) {
        List<Course> courses = courseRepository.findByTeacher_TeacherIdOrderByCreatedAtDesc(userId);
        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    private CourseResponse mapToCourseResponse(Course course) {
        String teacherName = course.getTeacher() != null
                ? course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName()
                : "Unknown Teacher";

        String categoryName = course.getCategory() != null
                ? course.getCategory().getCategoryName()
                : "Uncategorized";

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .imageUrl(course.getImageUrl())
                .price(course.getPrice())
                .createdAt(course.getCreatedAt())
                .archiveStatus(course.getArchiveStatus())
                .teacherName(teacherName)
                .categoryName(categoryName)
                .build();
    }
}