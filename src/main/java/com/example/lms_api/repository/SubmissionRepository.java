// repository/SubmissionRepository.java
package com.example.lms_api.repository;

import com.example.lms_api.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    @Query(value = "SELECT * FROM submission WHERE CAST(studentid AS VARCHAR) = CAST(:studentId AS VARCHAR) AND aqid = :aqId", nativeQuery = true)
    List<Submission> findByStudentIdAndAqId(@Param("studentId") Integer studentId, @Param("aqId") String aqId);
}