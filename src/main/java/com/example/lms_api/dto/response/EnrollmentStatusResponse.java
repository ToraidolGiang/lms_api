package com.example.lms_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response thuần GET, không side-effect — dùng để:
 *  1) Biết student hiện tại đã mua (enrolled) khóa học này hay chưa.
 *  2) Lấy enrollmentId hợp lệ để gửi kèm khi tạo review.
 *
 * Khác với PaymentCheckoutResponse: không tạo Payment "Pending" / link PayOS
 * khi gọi, vì chỉ query thẳng EnrollmentRepository.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentStatusResponse {
    private boolean enrolled;
    private Integer enrollmentId;
}
