package com.example.lms_api.repository;

import com.example.lms_api.entity.Gradebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GradebookRepository extends JpaRepository<Gradebook, String> {

    @Query(value = "SELECT g.* FROM gradebook g " +
            "JOIN submission s ON g.submissionid = s.submissionid " +
            "WHERE s.studentid = :studentId AND s.aqid = :lessonId " +
            "ORDER BY g.gradedat DESC LIMIT 1", nativeQuery = true)
    Optional<Gradebook> findLatestGradeByStudentAndLesson(@Param("studentId") Integer studentId, @Param("lessonId") String lessonId);
}