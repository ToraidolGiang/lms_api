package com.example.lms_api.dto.request.learning;
import lombok.Data;

@Data
public class SubmitAssignmentRequest {
    private String fileUrl; // Link Google Drive hoặc Cloudinary
    private String studentNotes; // Ghi chú thêm của học viên (nếu có)
}