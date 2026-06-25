package com.example.lms_api.service;

import com.example.lms_api.dto.request.CourseGradeRequest;
import com.example.lms_api.dto.request.SubmitGradeRequest;
import com.example.lms_api.dto.response.CourseGradeResponse;
import com.example.lms_api.dto.response.GradingListResponse;

import java.util.List;

public interface CourseGradeService {

    // CRUD cũ
    CourseGradeResponse createGrade(CourseGradeRequest request);
    CourseGradeResponse updateGrade(Integer id, CourseGradeRequest request);
    CourseGradeResponse toggleMask(Integer id, Boolean masked);
    List<CourseGradeResponse> getGradesByCourse(Integer courseId);
    List<CourseGradeResponse> getGradesByStudentForStudent(Integer studentId);
    void submitOrUpdateGrade(CourseGradeRequest request);

    // Grading feature
    GradingListResponse getGradingList(Integer courseId);
    GradingListResponse.StudentGradingItem submitGrade(SubmitGradeRequest request);
}