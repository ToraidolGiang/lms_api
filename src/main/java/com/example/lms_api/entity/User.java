package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password",nullable = false)
    private String passwordHash;

    @Column(name = "ImageUrl")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(insertable = false)
    Date createdAt;

    @Builder.Default
    @Column(name = "isActive",nullable = false)
    private boolean active = true;

    public enum Role {
        STUDENT, TEACHER, ADMIN
    }
}