package com.example.lms_api.repository;

import com.example.lms_api.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByTeacherTeacherId(Integer teacherId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.teacher.teacherId = :teacherId")
    long countByTeacherId(@Param("teacherId") Integer teacherId);

    List<Course> findByIsDeletedFalseAndIsActiveTrue();
    List<Course> findByCategoryCategoryId(Integer categoryId);
    List<Course> findByArchiveStatus(String archiveStatus);

    List<Course> findByTeacher_TeacherIdOrderByCreatedAtDesc(Integer teacherId);
    long countByTeacher_TeacherId(Integer teacherId);
}