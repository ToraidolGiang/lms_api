package com.example.lms_api.repository;

import com.example.lms_api.entity.CourseReview;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseReviewRepository extends MongoRepository<CourseReview, String> {
    // Tìm danh sách review dựa trên ID khóa học, sắp xếp theo thời gian mới nhất
    List<CourseReview> findByCourseIdOrderByCreatedAtDesc(Integer courseId);

    @Aggregation(pipeline = {
            "{ '$match': { 'courseId': ?0 } }",
            "{ '$group': { '_id': null, 'avgRating': { '$avg': '$rating' } } }"
    })
    Double getAverageRatingByCourseId(Integer courseId);
}