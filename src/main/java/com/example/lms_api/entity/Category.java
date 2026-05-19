package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL trong PostgreSQL tự động tăng
    @Column(name = "CategoryID")
    private Integer categoryId;

    @Column(name = "CategoryName", length = 50)
    private String categoryName;

    // Quan hệ 1-N với bảng Courses (Không bắt buộc nhưng nên có để dễ truy vấn ngược)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Course> courses;
}