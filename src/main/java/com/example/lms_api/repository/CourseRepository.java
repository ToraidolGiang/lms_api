package com.example.lms_api.repository;


import com.example.lms_api.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Integer> { // Đổi từ String sang Integer cho đúng khóa chính

    // Tìm danh sách khóa học theo TeacherID (Giả định trong TeacherEntity bạn đặt tên trường Id là teacherId)
    List<CourseEntity> findByTeacherTeacherId(Integer teacherId);

    // Tiện tay tặng bạn thêm vài hàm hay dùng dựa trên các trường trong DB của bạn:

    // Tìm các khóa học chưa bị xóa mềm và đang hoạt động
    List<CourseEntity> findByIsDeletedFalseAndIsActiveTrue();

    // Tìm khóa học theo danh mục (CategoryID)
    List<CourseEntity> findByCategoryCategoryId(Integer categoryId);

    // Tìm khóa học theo trạng thái lưu trữ (Active, Archived, Deleted)
    List<CourseEntity> findByArchiveStatus(String archiveStatus);
}