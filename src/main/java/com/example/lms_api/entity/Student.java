package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Thêm dòng này để tự động tăng
    @Column(name = "student_id", nullable = false, updatable = false) // Đã bỏ 'length = 50' vì đây là số nguyên
    private Integer studentId;

    // Ánh xạ khóa ngoại (Foreign Key) tới UserEntity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender;

    @Column(name = "location")
    private String location;

    @Column(name = "phone", length = 20)
    private String phone;

    // Sử dụng @Lob hoặc columnDefinition = "TEXT" cho kiểu dữ liệu TEXT lớn

    @Column(name = "Bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "school", length = 50)
    private String school;
}