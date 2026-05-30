package com.example.lms_api.repository;

import com.example.lms_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findFirstByCourse_CourseIdAndStudent_StudentIdOrderByPaymentIdDesc(Integer courseId, Integer studentId);
}
