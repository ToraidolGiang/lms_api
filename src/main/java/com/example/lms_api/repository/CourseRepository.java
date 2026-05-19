package com.example.lms_api.repository;


import com.example.lms_api.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> { // Đổi từ String sang Integer cho đúng khóa chính

    // Tìm danh sách khóa học theo TeacherID (Giả định trong TeacherEntity bạn đặt tên trường Id là teacherId)
    List<Course> findByTeacherTeacherId(Integer teacherId);

    // Tiện tay tặng bạn thêm vài hàm hay dùng dựa trên các trường trong DB của bạn:

    // Tìm các khóa học chưa bị xóa mềm và đang hoạt động
    List<Course> findByIsDeletedFalseAndIsActiveTrue();

    // Tìm khóa học theo danh mục (CategoryID)
    List<Course> findByCategoryCategoryId(Integer categoryId);

    // Tìm khóa học theo trạng thái lưu trữ (Active, Archived, Deleted)
    List<Course> findByArchiveStatus(String archiveStatus);
}