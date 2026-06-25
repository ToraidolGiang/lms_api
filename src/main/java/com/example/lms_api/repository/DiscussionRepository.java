package com.example.lms_api.repository;

import com.example.lms_api.entity.Discussion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends MongoRepository<Discussion, String> {

    // Tìm toàn bộ thảo luận của 1 bài học, sắp xếp mới nhất lên đầu
    List<Discussion> findByCourseIdAndLessonIdOrderByCreatedAtDesc(Integer courseId, String lessonId);

    // ── Dashboard: Hoạt động gần đây ────────────────────────────────────────

    /**
     * Lấy N câu hỏi gần nhất (authorRole='STUDENT') thuộc các khoá học
     * của teacher (courseId IN danh sách).
     * Dùng cho mục "Đặt câu hỏi" trong "Hoạt động gần đây".
     */
    @Query("{ 'courseId': { $in: ?0 }, 'authorRole': 'STUDENT' }")
    List<Discussion> findRecentStudentDiscussionsByCourseIds(
            List<Integer> courseIds,
            Pageable pageable);
}