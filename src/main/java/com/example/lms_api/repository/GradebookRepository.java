package com.example.lms_api.repository;

import com.example.lms_api.entity.Gradebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradebookRepository extends JpaRepository<Gradebook, String> {

    @Query(value = "SELECT g.* FROM gradebook g " +
            "JOIN submission s ON g.submissionid = s.submissionid " +
            "WHERE CAST(s.studentid AS VARCHAR) = CAST(:studentId AS VARCHAR) AND s.aqid = :lessonId " +
            "ORDER BY g.gradedat DESC LIMIT 1", nativeQuery = true)
    Optional<Gradebook> findLatestGradeByStudentAndLesson(
            @Param("studentId") Integer studentId,
            @Param("lessonId") String lessonId);

    // Lấy grade mới nhất theo submissionId (dùng khi tính quizAvg)
    Optional<Gradebook> findTopBySubmissionIdOrderByGradedAtDesc(String submissionId);

    // ── Dashboard queries ────────────────────────────────────────────────────
    // NOTE: Tương tự SubmissionRepository — dùng đường gradebook→submission→student→enrollment→course→teacher
    //       thay vì JOIN course_content (bảng đó ở MongoDB, không có trong PostgreSQL).

    /**
     * Tính điểm trung bình của tất cả bài đã chấm thuộc khoá học của teacher.
     * Dùng cho thẻ "Điểm TB" trên màn Home.
     *
     * Đường đi: gradebook → submission.studentId → enrollment → course → teacher
     */
    @Query(value = """
        SELECT avg(cg.final_score) AS pendingCount
        FROM courses c
        LEFT JOIN course_grade cg ON c.courseid = cg.courseid
        WHERE cg.is_masked = true AND c.teacherid = :teacherId
        GROUP BY c.courseid, c.title
    """, nativeQuery = true)
    Double avgScoreByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * Lấy danh sách bài vừa được chấm điểm gần đây của teacher.
     * Dùng cho "Hoạt động gần đây" – mục "nhận điểm XX".
     *
     * Trả về: submissionId, studentId, courseId, courseTitle, score, gradedAt
     */
    @Query(value = """
        SELECT g.submissionid AS submissionId,
               s.studentid   AS studentId,
               c.courseid    AS courseId,
               c.title       AS courseTitle,
               g.score       AS score,
               g.gradedat    AS gradedAt
        FROM gradebook g
        JOIN submission s ON g.submissionid = s.submissionid
        JOIN (
            SELECT DISTINCT ON (e.student_id) e.student_id, e.course_id
            FROM enrollment e
            JOIN courses c2 ON e.course_id = c2.courseid
            WHERE c2.teacherid = :teacherId
              AND e.access_status = 'Active'
            ORDER BY e.student_id, e.enroll_date DESC
        ) latest_enroll ON s.studentid = latest_enroll.student_id
        JOIN courses c ON latest_enroll.course_id = c.courseid
        ORDER BY g.gradedat DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findRecentGradedByTeacherId(
            @Param("teacherId") Integer teacherId,
            @Param("limit") int limit);
}