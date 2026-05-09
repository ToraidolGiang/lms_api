package com.example.lms_api.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

}
