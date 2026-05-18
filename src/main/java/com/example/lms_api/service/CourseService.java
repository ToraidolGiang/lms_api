package com.example.lms_api.service;


import com.example.lms_api.dto.request.CourseRequest;
import com.example.lms_api.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    List<CourseResponse> getAllActiveCourses();
    CourseResponse getCourseById(Integer id);
    CourseResponse updateCourse(Integer id, CourseRequest request);
    void deleteCourse(Integer id, String deletedBy, String reason);
}