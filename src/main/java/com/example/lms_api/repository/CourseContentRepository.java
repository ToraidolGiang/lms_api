package com.example.lms_api.repository;

import com.example.lms_api.entity.CourseContent;
import org.bson.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseContentRepository extends MongoRepository<CourseContent, String> {

    Optional<CourseContent> findByCourseId(Integer courseId);

    boolean existsByCourseId(Integer courseId);

    void deleteByCourseId(Integer courseId);

    @Query(value = "{ 'courseId' : ?0 }", fields = "{ 'metadata.totalLessons' : 1, '_id' : 0 }")
    Optional<Document> findMetadataOnlyByCourseId(Integer courseId);
}