package com.example.lms_api.dto.request;


import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    private Integer courseId; // Nếu tự điền bằng tay
    @NotNull(message = "Thiếu mã giảng viên")
    private Integer teacherId;
    @NotNull(message = "Thiếu mã danh mục")
    private Integer categoryId;
    @NotBlank(message = "Tiêu đề khóa học không được rỗng")
    @Size(min = 5, max = 100, message = "Tiêu đề phải từ 5 đến 100 ký tự")
    private String title;
    @NotBlank(message = "Mô tả không được rỗng")
    @Size(min = 20, message = "Mô tả khóa học tối thiểu 20 ký tự")
    private String description;
    @NotBlank(message = "Ảnh khóa học không được rỗng")
    private String imageUrl;
    @NotNull(message = "Giá không được rỗng")
    @Min(value = 0, message = "Giá không được nhỏ hơn 0")
    private BigDecimal price;
}