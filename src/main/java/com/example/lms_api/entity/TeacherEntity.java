package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Thêm dòng này để tự động tăng
    @Column(name = "TeacherID", nullable = false, updatable = false) // Đã bỏ 'length = 50' vì đây là số nguyên
    private Integer teacherId;

    // Ánh xạ khóa ngoại (Foreign Key) tới UserEntity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "FirstName", length = 50)
    private String firstName;

    @Column(name = "LastName", length = 50)
    private String lastName;

    @Column(name = "BirthDate")
    private LocalDate birthDate;

    @Column(name = "Location")
    private String location;

    @Column(name = "Phone", length = 20)
    private String phone;

    // Sử dụng @Lob hoặc columnDefinition = "TEXT" cho kiểu dữ liệu TEXT lớn
    @Lob
    @Column(name = "Bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "Degree", length = 50)
    private String degree;
}