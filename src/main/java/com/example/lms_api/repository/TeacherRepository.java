package com.example.lms_api.repository;

import com.example.lms_api.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,String> {

    @Query("SELECT t FROM Teacher t WHERE t.user.id = :teacherId")
    Optional<Teacher> findByTeacherId(@Param("teacherId") Integer teacherId);
    // Thêm hàm update
    @Modifying
    @Query("UPDATE Teacher t SET t.firstName = :firstName, t.lastName = :lastName, " +
            "t.birthDate = :birthDate, t.location = :location, t.phone = :phone, t.bio = :bio " +
            "WHERE t.teacherId = :teacherId")
    int updateTeacherInfo(@Param("teacherId") Integer teacherId,
                          @Param("firstName") String firstName,
                          @Param("lastName") String lastName,
                          @Param("birthDate") LocalDate birthDate,
                          @Param("location") String location,
                          @Param("phone") String phone,
                          @Param("bio") String bio);

}
