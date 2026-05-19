package com.example.lms_api.repository;

import com.example.lms_api.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUser_Id(Integer userId);
    Optional<Student> findByPhone(String phone);
}