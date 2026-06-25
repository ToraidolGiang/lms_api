package com.example.lms_api.repository;

import com.example.lms_api.entity.CourseGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseGradeRepository extends JpaRepository<CourseGrade, Integer> {

    List<CourseGrade> findByCourseId(Integer courseId);

    List<CourseGrade> findByStudentId(Integer studentId);

    // Student chỉ thấy điểm chưa bị ẩn
    List<CourseGrade> findByStudentIdAndIsMaskedFalse(Integer studentId);

    List<CourseGrade> findByCourseIdAndIsMaskedFalse(Integer courseId);

    Optional<CourseGrade> findByStudentIdAndCourseId(Integer studentId, Integer courseId);
}
