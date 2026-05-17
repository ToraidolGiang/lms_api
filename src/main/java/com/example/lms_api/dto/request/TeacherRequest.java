package com.example.lms_api.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TeacherRequest {
    // Không cần teacherId ở đây vì ta truyền nó qua URL
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String location;
    private String phone;
    private String bio;
    private String degree;
}