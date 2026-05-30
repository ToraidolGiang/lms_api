package com.example.lms_api.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {

//    @NotBlank(message = "Email không được để trống")
//    @Email(message = "Email không hợp lệ")
    private String email;

//    @NotBlank(message = "Username không được để trống")
//    @Size(min = 3, max = 50, message = "Username phải từ 3–50 ký tự")
    private String username;

//    @NotBlank(message = "Mật khẩu không được để trống")
//    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;

    // STUDENT | TEACHER  (ADMIN chỉ được tạo nội bộ)
    private String role;
}