package com.example.lms_api.repository;

import com.example.lms_api.entity.CourseContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseContentRepository extends MongoRepository<CourseContent, String> {

    Optional<CourseContent> findByCourseId(Integer courseId);

    boolean existsByCourseId(Integer courseId);

    void deleteByCourseId(Integer courseId);
}