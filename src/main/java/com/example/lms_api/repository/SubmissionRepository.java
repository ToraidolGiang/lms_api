// repository/SubmissionRepository.java
package com.example.lms_api.repository;

import com.example.lms_api.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    Optional<Submission> findTopByStudentIdAndTypeOrderBySubmittedAtDesc(
            Integer studentId, String type);

    // Lấy tất cả submission quiz của student (để tính quizAvgScore)
    List<Submission> findByStudentIdAndType(Integer studentId, String type);

}