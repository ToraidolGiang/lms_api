// repository/StudentProgressRepository.java
package com.example.lms_api.repository;

import com.example.lms_api.entity.document.StudentProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProgressRepository extends MongoRepository<StudentProgress, String> {
    Optional<StudentProgress> findByStudentIdAndCourseId(Integer studentId, Integer courseId);
}