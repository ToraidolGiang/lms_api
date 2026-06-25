// repository/SubmissionRepository.java
package com.example.lms_api.repository;

import com.example.lms_api.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {

    Optional<Submission> findTopByStudentIdAndTypeOrderBySubmittedAtDesc(
            Integer studentId, String type);

    // Lấy tất cả submission quiz của student (để tính quizAvgScore)
    List<Submission> findByStudentIdAndType(Integer studentId, String type);

    // ── Dashboard queries ────────────────────────────────────────────────────
    // NOTE: submission.aqId là lessonId lưu trong MongoDB → KHÔNG thể JOIN trực tiếp với PostgreSQL.
    //       Giải pháp: dùng đường submission → studentId → enrollment → course → teacher.
    //       Một học sinh chỉ enroll vào 1 course của teacher (access_status='Active'),
    //       nên kết quả vẫn đúng trong thực tế.

    /**
     * Đếm tổng số bài tự luận (type='assignment') chưa được chấm
     * của tất cả học sinh đang enroll vào khoá học của teacher.
     *
     * Logic:
     *   - Tìm studentId đang enroll khoá học của teacher (access_status='Active')
     *   - Đếm submission type='assignment' của những student đó chưa có trong gradebook
     */
    @Query(value = """
        SELECT COUNT(s.submissionid)
        FROM submission s
        WHERE s.type = 'assignment'
          AND s.studentid IN (
              SELECT DISTINCT e.student_id
              FROM enrollment e
              JOIN courses c ON e.course_id = c.courseid
              WHERE c.teacherid = :teacherId
                AND e.access_status = 'Active'
          )
          AND s.submissionid NOT IN (SELECT g.submissionid FROM gradebook g)
        """, nativeQuery = true)
    long countPendingGradingByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * Lấy danh sách bài nộp gần đây của các học sinh trong khoá học của teacher.
     * Kèm courseId và courseTitle lấy từ enrollment (lấy course enroll mới nhất của student).
     *
     * Trả về: submissionId, studentId, courseId, courseTitle, type, submittedAt
     */
    @Query(value = """
        SELECT s.submissionid  AS submissionId,
               s.studentid     AS studentId,
               c.courseid      AS courseId,
               c.title         AS courseTitle,
               s.type          AS type,
               s.submittedat   AS submittedAt
        FROM submission s
        JOIN (
            SELECT DISTINCT ON (e.student_id) e.student_id, e.course_id
            FROM enrollment e
            JOIN courses c2 ON e.course_id = c2.courseid
            WHERE c2.teacherid = :teacherId
              AND e.access_status = 'Active'
            ORDER BY e.student_id, e.enroll_date DESC
        ) latest_enroll ON s.studentid = latest_enroll.student_id
        JOIN courses c ON latest_enroll.course_id = c.courseid
        ORDER BY s.submittedat DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findRecentSubmissionsByTeacherId(
            @Param("teacherId") Integer teacherId,
            @Param("limit") int limit);

    /**
     * Đếm số bài nộp của teacher theo từng ngày trong tuần.
     * Dùng cho biểu đồ "Hoạt động tuần này".
     *
     * Trả về: dayOfWeek (0=CN, 1=T2...6=T7 theo PostgreSQL EXTRACT(DOW)), count
     */
    @Query(value = """
        SELECT EXTRACT(DOW FROM s.submittedat) AS dayOfWeek,
               COUNT(s.submissionid)           AS cnt
        FROM submission s
        WHERE s.studentid IN (
            SELECT DISTINCT e.student_id
            FROM enrollment e
            JOIN courses c ON e.course_id = c.courseid
            WHERE c.teacherid = :teacherId
        )
          AND s.submittedat >= :startOfWeek
          AND s.submittedat <  :endOfWeek
        GROUP BY EXTRACT(DOW FROM s.submittedat)
        """, nativeQuery = true)
    List<Object[]> countSubmissionsPerDayOfWeek(
            @Param("teacherId") Integer teacherId,
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("endOfWeek") LocalDateTime endOfWeek);

    /**
     * Đếm tổng số bài nộp của teacher trong khoảng thời gian.
     * Dùng để tính % thay đổi tuần này vs tuần trước.
     */
    @Query(value = """
        SELECT COUNT(s.submissionid)
        FROM submission s
        WHERE s.studentid IN (
            SELECT DISTINCT e.student_id
            FROM enrollment e
            JOIN courses c ON e.course_id = c.courseid
            WHERE c.teacherid = :teacherId
        )
          AND s.submittedat >= :start
          AND s.submittedat <  :end
        """, nativeQuery = true)
    long countSubmissionsInRange(
            @Param("teacherId") Integer teacherId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Lấy danh sách khoá học có bài tự luận chưa chấm, kèm số lượng.
     * Dùng cho danh sách task "Cần làm ngay".
     *
     * Nhóm theo course (từ enrollment của student), đếm số submission chưa chấm.
     * Trả về: courseId, courseTitle, pendingCount
     */
    @Query(value = """
        SELECT c.courseid    AS courseId,
               c.title       AS courseTitle,
               COUNT(s.submissionid) AS pendingCount
        FROM submission s
        JOIN (
            SELECT DISTINCT ON (e.student_id) e.student_id, e.course_id
            FROM enrollment e
            JOIN courses c2 ON e.course_id = c2.courseid
            WHERE c2.teacherid = :teacherId
              AND e.access_status = 'Active'
            ORDER BY e.student_id, e.enroll_date DESC
        ) latest_enroll ON s.studentid = latest_enroll.student_id
        JOIN courses c ON latest_enroll.course_id = c.courseid
        WHERE s.type = 'assignment'
          AND s.submissionid NOT IN (SELECT g.submissionid FROM gradebook g)
        GROUP BY c.courseid, c.title
        ORDER BY pendingCount DESC
        """, nativeQuery = true)
    List<Object[]> findPendingGradingGroupedByCourse(@Param("teacherId") Integer teacherId);
}