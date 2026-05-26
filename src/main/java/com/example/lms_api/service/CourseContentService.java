package com.example.lms_api.service;

import com.example.lms_api.dto.response.CourseContentResponse;

public interface CourseContentService {
    CourseContentResponse getCourseContent(Integer courseId);
}