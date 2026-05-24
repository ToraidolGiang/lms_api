package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.PaymentCheckoutRequest;
import com.example.lms_api.dto.request.PaymentWebhookRequest;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthenticated");
        }
        // UserDetailsServiceImpl set username = userId
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

        // Nếu đã active enrollment thì không tạo payment mới
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

        // Chuỗi QR demo: client render QR từ chuỗi này
        String qrText = "LMS|PAYMENT=" + saved.getPaymentId() + "|AMOUNT=" + saved.getAmount();

        return PaymentCheckoutResponse.builder()
                .paymentId(saved.getPaymentId())
                .amount(saved.getAmount())
                .paymentStatus(saved.getPaymentStatus())
                .qrText(qrText)
                .build();
    }

    @Override
    @Transactional
    public PaymentWebhookResponse handleWebhook(PaymentWebhookRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment không tồn tại!"));

        String newStatus = request.getPaymentStatus();
        if (newStatus == null || newStatus.isBlank()) {
            throw new RuntimeException("paymentStatus is required");
        }

        payment.setPaymentStatus(newStatus);
        if ("Paid".equalsIgnoreCase(newStatus)) {
            payment.setPaymentDate(LocalDateTime.now());

            // tạo enrollment nếu chưa có
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
