package com.example.lms_api.repository;

import com.example.lms_api.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    boolean existsByCourse_CourseIdAndStudent_StudentIdAndAccessStatus(Integer courseId, Integer studentId, String accessStatus);

    Optional<Enrollment> findFirstByCourse_CourseIdAndStudent_StudentIdOrderByEnrollDateDesc(Integer courseId, Integer studentId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.teacher.teacherId = :teacherId")
    long countStudentsByTeacherId(@Param("teacherId") Integer teacherId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.courseId = :courseId")
    long countByCourseId(@Param("courseId") Integer courseId);

    // ĐÃ SỬA: Lấy giá (price) thông qua bảng Enrollment nối với Course
    @Query("SELECT COALESCE(SUM(e.course.price), 0) FROM Enrollment e WHERE e.course.teacher.teacherId = :teacherId")
    Double sumRevenueByTeacherId(@Param("teacherId") Integer teacherId);

    @Query("""
        SELECT COUNT(DISTINCT e.student.id)
        FROM Enrollment e
        WHERE e.course.teacher.teacherId = :teacherId
    """)
    long countDistinctStudentsByTeacherId(@Param("teacherId") Integer teacherId);

    // ĐÃ SỬA: Đổi e.enrolledAt thành e.enrollDate (hoặc đổi lại cho đúng tên biến trong Entity Enrollment của bạn)
    @Query("""
        SELECT COUNT(e)
        FROM Enrollment e
        WHERE e.course.teacher.teacherId = :teacherId
          AND MONTH(e.enrollDate) = MONTH(CURRENT_DATE)
          AND YEAR(e.enrollDate)  = YEAR(CURRENT_DATE)
    """)
    long countEnrollmentsThisMonth(@Param("teacherId") Integer teacherId);
}