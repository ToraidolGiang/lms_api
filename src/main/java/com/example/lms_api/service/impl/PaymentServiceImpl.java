package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.PaymentCheckoutRequest;
import com.example.lms_api.dto.request.PayOSWebhookRequest;
import com.example.lms_api.dto.response.PaymentCheckoutResponse;
import com.example.lms_api.dto.response.PaymentWebhookResponse;
import com.example.lms_api.entity.Course;
import com.example.lms_api.entity.Enrollment;
import com.example.lms_api.entity.Payment;
import com.example.lms_api.entity.Student;
import com.example.lms_api.repository.CourseRepository;
import com.example.lms_api.repository.EnrollmentRepository;
import com.example.lms_api.repository.PaymentRepository;
import com.example.lms_api.repository.StudentRepository;
import com.example.lms_api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final PayOS payOS;

    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthenticated");
        }
        return Integer.parseInt(auth.getName());
    }

    private Student currentStudent() {
        Integer userId = currentUserId();
        return studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found for userId=" + userId));
    }

    @Override
    @Transactional
    public PaymentCheckoutResponse checkout(PaymentCheckoutRequest request) {
        Student student = currentStudent();
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course không tồn tại!"));

        boolean purchased = enrollmentRepository.existsByCourse_CourseIdAndStudent_StudentIdAndAccessStatus(
                course.getCourseId(), student.getStudentId(), "Active");
        if (purchased) {
            return PaymentCheckoutResponse.builder()
                    .paymentId(null)
                    .amount(course.getPrice())
                    .paymentStatus("Paid")
                    .qrText(null)
                    .build();
        }

        Payment payment = Payment.builder()
                .course(course)
                .student(student)
                .amount(course.getPrice())
                .paymentStatus("Pending")
                .paymentDate(null)
                .build();

        Payment saved = paymentRepository.save(payment);

        try {
            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(saved.getPaymentId().longValue())
                    .amount(saved.getAmount().longValue())
                    .description("Thanh toan khoa hoc")
                    .returnUrl("https://localhost:8080/success")
                    .cancelUrl("https://localhost:8080/cancel")
                    .build();

            CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);

            return PaymentCheckoutResponse.builder()
                    .paymentId(saved.getPaymentId())
                    .amount(saved.getAmount())
                    .paymentStatus(saved.getPaymentStatus())
                    .qrText(null) // Để null vì mình không tự vẽ QR nữa
                    .checkoutUrl(data.getCheckoutUrl()) // SỬA Ở ĐÂY: Gán link của PayOS vào đúng trường checkoutUrl
                    .build();

        } catch (Exception e) {
            System.err.println("======== LỖI TỪ PAYOS ========");
            e.printStackTrace();
            System.err.println("==============================");
            throw new RuntimeException("Lỗi tạo PayOS link: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentWebhookResponse handleWebhook(PayOSWebhookRequest request) {
        if (request.getData() == null || request.getData().getOrderCode() == null) {
            throw new RuntimeException("Dữ liệu webhook từ PayOS không hợp lệ");
        }

        // Đọc mã giao dịch từ orderCode của PayOS
        Integer paymentId = request.getData().getOrderCode();
        Payment payment = paymentRepository.findById(paymentId).orElse(null);

        // NẾU PAYOS GỬI MÃ TEST (Không có trong DB) -> Vẫn trả về OK để PayOS chịu lưu link
        if (payment == null) {
            System.out.println("===> Nhận request test Webhook từ PayOS cho mã: " + paymentId);
            return PaymentWebhookResponse.builder()
                    .paymentId(paymentId)
                    .paymentStatus("Test_OK")
                    .enrollmentId(null)
                    .build();
        }

        // Kiểm tra trạng thái: Mã "00" đại diện cho thanh toán thành công
        String newStatus = "00".equals(request.getCode()) ? "Paid" : "Failed";

        payment.setPaymentStatus(newStatus);

        if ("Paid".equalsIgnoreCase(newStatus)) {
            payment.setPaymentDate(LocalDateTime.now());

            // Tìm kiếm hoặc tạo mới thông tin ghi danh khóa học cho học viên
            Enrollment enrollment = enrollmentRepository
                    .findFirstByCourse_CourseIdAndStudent_StudentIdOrderByEnrollDateDesc(
                            payment.getCourse().getCourseId(),
                            payment.getStudent().getStudentId())
                    .orElse(null);

            if (enrollment == null || !"Active".equalsIgnoreCase(enrollment.getAccessStatus())) {
                enrollment = Enrollment.builder()
                        .course(payment.getCourse())
                        .student(payment.getStudent())
                        .enrollDate(LocalDateTime.now())
                        .accessStatus("Active")
                        .canAccessAfterDeletion(true)
                        .accessExpiryDate(null)
                        .build();
                enrollment = enrollmentRepository.save(enrollment);
            }

            paymentRepository.save(payment);

            return PaymentWebhookResponse.builder()
                    .paymentId(payment.getPaymentId())
                    .paymentStatus(payment.getPaymentStatus())
                    .enrollmentId(enrollment.getEnrollmentId())
                    .build();
        }

        paymentRepository.save(payment);
        return PaymentWebhookResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentStatus(payment.getPaymentStatus())
                .enrollmentId(null)
                .build();
    }
}