// repository/SubmissionRepository.java
package com.example.lms_api.repository;

import com.example.lms_api.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
}