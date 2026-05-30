package com.example.lms_api.repository;

import com.example.lms_api.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    boolean existsByCourse_CourseIdAndStudent_StudentIdAndAccessStatus(Integer courseId, Integer studentId, String accessStatus);

    Optional<Enrollment> findFirstByCourse_CourseIdAndStudent_StudentIdOrderByEnrollDateDesc(Integer courseId, Integer studentId);
}
