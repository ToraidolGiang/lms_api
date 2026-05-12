package com.example.lms_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    private Integer teacherId;
    private Integer userId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String location;
    private String phone;
    private String bio;
    private String degree;
}