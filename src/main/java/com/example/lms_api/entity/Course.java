package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    // Nếu trong DB bạn có cấu cụ thể cho CourseID tự tăng thì thêm @GeneratedValue,
    // còn nếu nhập tay bằng cơm thì giữ nguyên như thế này.
    @Column(name = "CourseID")
    private Integer courseId;

    @Column(name = "Title", length = 200)
    private String title;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ImageUrl", length = 500)
    private String imageUrl;

    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "DeletedBy", length = 50)
    private String deletedBy;

    @Column(name = "DeleteReason", length = 500)
    private String deleteReason;

    @Column(name = "ArchiveStatus", length = 20)
    private String archiveStatus;

    // Mối quan hệ Nhiều - Một với bảng Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID")
    private Category category;

    // Mối quan hệ Nhiều - Một với bảng Teacher (Thay vì UserEntity như bản nháp của bạn)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TeacherID")
    private Teacher teacher;
}