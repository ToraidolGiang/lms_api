package com.example.lms_api.repository;

import com.example.lms_api.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUser_Id(Integer userId);
    Optional<Student> findByPhone(String phone);
    @Modifying
    @Query("UPDATE Student s SET s.firstName = :firstName, s.lastName = :lastName, " +
            "s.birthDate = :birthDate, s.location = :location, s.phone = :phone, s.school = :school " +
            "WHERE s.studentId = :studentId")
    int updateStudentInfo(@Param("studentId") Integer studentId,
                          @Param("firstName") String firstName,
                          @Param("lastName") String lastName,
                          @Param("birthDate") LocalDate birthDate,
                          @Param("location") String location,
                          @Param("phone") String phone,
                          @Param("school") String school);
}