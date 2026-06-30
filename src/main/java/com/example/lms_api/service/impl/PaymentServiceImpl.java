package com.example.lms_api.service.impl;

import com.example.lms_api.dto.request.PaymentCheckoutRequest;
import com.example.lms_api.dto.response.PaymentCheckoutResponse;
import com.example.lms_api.dto.response.PaymentWebhookResponse;
import com.example.lms_api.dto.request.NotificationRequest;
import com.example.lms_api.service.NotificationService;
import com.example.lms_api.entity.Course;
import com.example.lms_api.entity.Enrollment;
import com.example.lms_api.entity.Payment;
import com.example.lms_api.entity.Student;
import com.example.lms_api.repository.CourseRepository;
import com.example.lms_api.repository.EnrollmentRepository;
import com.example.lms_api.repository.PaymentRepository;
import com.example.lms_api.service.PaymentService;
import com.example.lms_api.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${payos.webhook.verify-signature:true}")
    private boolean verifySignature;

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final CourseRepository courseRepository;
    private final SecurityUtil securityUtil;
    private final PayOS payOS;

    @Override
    @Transactional
    public PaymentCheckoutResponse checkout(PaymentCheckoutRequest request) {
        Student student = securityUtil.getCurrentStudent();
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
            String timeString = String.valueOf(System.currentTimeMillis());
            String prefix = "1" + timeString.substring(timeString.length() - 7);
            String orderCodeStr = prefix + saved.getPaymentId();
            long orderCode = Long.parseLong(orderCodeStr);

            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
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
    public PaymentWebhookResponse handleWebhook(Webhook body) {
        WebhookData verifiedData;
        
        if (verifySignature) {
            // Xác thực chữ ký webhook sử dụng SDK PayOS
            try {
                verifiedData = payOS.webhooks().verify(body);
            } catch (Exception e) {
                throw new RuntimeException("Xác thực chữ ký webhook thất bại: " + e.getMessage());
            }
        } else {
            // Chế độ DEV/TEST: bypass xác thực chữ ký để test nhanh bằng Postman
            verifiedData = body.getData();
        }

        if (verifiedData == null || verifiedData.getOrderCode() == null) {
            throw new RuntimeException("Dữ liệu webhook từ PayOS không hợp lệ");
        }

        // Đọc mã giao dịch từ orderCode của PayOS
        String orderCodeStr = String.valueOf(verifiedData.getOrderCode());
        Integer paymentId;
        if (orderCodeStr.length() > 8 && orderCodeStr.startsWith("1")) {
            paymentId = Integer.parseInt(orderCodeStr.substring(8));
        } else {
            paymentId = verifiedData.getOrderCode().intValue();
        }
        
        // Sử dụng khóa bi quan (SELECT FOR UPDATE) để đồng bộ đa luồng tránh Race Condition
        Payment payment = paymentRepository.findByIdForUpdate(paymentId).orElse(null);

        // NẾU PAYOS GỬI MÃ TEST (Không có trong DB) -> Vẫn trả về OK để PayOS chịu lưu link
        if (payment == null) {
            System.out.println("===> Nhận request test Webhook từ PayOS cho mã: " + paymentId);
            return PaymentWebhookResponse.builder()
                    .paymentId(paymentId)
                    .paymentStatus("Test_OK")
                    .enrollmentId(null)
                    .build();
        }

        // KIỂM TRA ĐỘ ĐỘC TRỊ (Idempotency): Nếu giao dịch đã thanh toán rồi, trả về thành công sớm
        if ("Paid".equalsIgnoreCase(payment.getPaymentStatus())) {
            Enrollment activeEnrollment = enrollmentRepository
                    .findFirstByCourse_CourseIdAndStudent_StudentIdOrderByEnrollDateDesc(
                            payment.getCourse().getCourseId(),
                            payment.getStudent().getStudentId())
                    .orElse(null);

            return PaymentWebhookResponse.builder()
                    .paymentId(payment.getPaymentId())
                    .paymentStatus(payment.getPaymentStatus())
                    .enrollmentId(activeEnrollment != null ? activeEnrollment.getEnrollmentId() : null)
                    .build();
        }

        // Đọc mã trạng thái từ verifiedData hoặc body
        String code = verifiedData.getCode();
        if (code == null) {
            code = body.getCode();
        }
        String newStatus = "00".equals(code) ? "Paid" : "Failed";

        payment.setPaymentStatus(newStatus);

        if ("Paid".equalsIgnoreCase(newStatus)) {
            // Đối chiếu số tiền thực tế chuyển khoản
            if (verifiedData.getAmount() < payment.getAmount().intValue()) {
                throw new RuntimeException("Số tiền thanh toán thực tế nhỏ hơn giá trị hóa đơn!");
            }

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

            // Gửi thông báo cho học viên
            try {
                NotificationRequest notifReq = new NotificationRequest();
                // PHẢI dùng User.id (users.id) chứ KHÔNG PHẢI Student.studentId
                // vì Notification.userId lưu theo users.id
                int targetUserId = payment.getStudent().getUser().getId();
                notifReq.setTargetUserId(targetUserId);
                notifReq.setTitle("Thanh toán khóa học thành công");
                notifReq.setMessage("Bạn đã thanh toán thành công khóa học: " + payment.getCourse().getTitle());
                notifReq.setLink("course://" + payment.getCourse().getCourseId());
                notifReq.setType("PAYMENT");
                notificationService.createNotification(notifReq);
                System.out.println("===> ĐÃ TẠO THÔNG BÁO THANH TOÁN cho userId: " + targetUserId);
            } catch (Exception e) {
                System.err.println("Lỗi gửi thông báo thanh toán: " + e.getMessage());
                e.printStackTrace();
            }

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