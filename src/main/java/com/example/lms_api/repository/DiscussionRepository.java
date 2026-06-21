package com.example.lms_api.repository;

import com.example.lms_api.entity.Discussion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends MongoRepository<Discussion, String> {
    // Tìm toàn bộ thảo luận của 1 bài học, sắp xếp mới nhất lên đầu
    List<Discussion> findByCourseIdAndLessonIdOrderByCreatedAtDesc(Integer courseId, String lessonId);
}