package com.example.lms_api.repository;

import com.example.lms_api.entity.Course;
import com.example.lms_api.projection.CourseSummaryProjection;
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

    @Query(value = """
            SELECT 
                c.courseid AS courseId,
                c.title AS courseTitle,
                c.price AS price,
                c.image_url AS imageUrl,
                cat.category_name AS categoryName,
                CONCAT(t.last_name, ' ', t.first_name) AS teacherName,
                COALESCE(e.total_students, 0) AS totalStudents
            FROM 
                courses c
            LEFT JOIN 
                teacher t ON c.teacherid = t.teacherid
            LEFT JOIN 
                category cat ON c.categoryid = cat.categoryid
            LEFT JOIN 
                (
                    SELECT course_id, COUNT(DISTINCT student_id) AS total_students
                    FROM enrollment
                    GROUP BY course_id
                ) e ON c.courseid = e.course_id
            WHERE 
                c.is_active = true 
                AND (c.is_deleted = false OR c.is_deleted IS NULL)
            """, nativeQuery = true)
    List<CourseSummaryProjection> getExploreCourses();

    @Query(value = """
            SELECT 
                c.courseid AS courseId,
                c.title AS courseTitle,
                c.price AS price,
                c.image_url AS imageUrl,
                cat.category_name AS categoryName,
                CONCAT(t.last_name, ' ', t.first_name) AS teacherName,
                COALESCE(e.total_students, 0) AS totalStudents
            FROM 
                courses c
            LEFT JOIN 
                teacher t ON c.teacherid = t.teacherid
            LEFT JOIN 
                category cat ON c.categoryid = cat.categoryid
            LEFT JOIN 
                (
                    SELECT course_id, COUNT(DISTINCT student_id) AS total_students
                    FROM enrollment
                    GROUP BY course_id
                ) e ON c.courseid = e.course_id
            WHERE 
                c.is_active = true 
                AND (c.is_deleted = false OR c.is_deleted IS NULL)
                AND c.teacherid = :teacherId
            """, nativeQuery = true)
    List<CourseSummaryProjection> getExploreCourses(@Param("teacherId") Integer teacherId);
}