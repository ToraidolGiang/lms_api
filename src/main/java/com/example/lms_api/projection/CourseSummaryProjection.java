package com.example.lms_api.projection;

import java.math.BigDecimal;

public interface CourseSummaryProjection {
    Integer getCourseId();
    String getCourseTitle();
    BigDecimal getPrice();
    String getImageUrl();
    String getTeacherName();
    String getCategoryName();
    Integer getTotalStudents();
    Double getAverageRating();
    Integer getTotalLessons();
}