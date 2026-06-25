package com.example.lms_api.repository;

import com.example.lms_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    @Procedure(procedureName = "create_user_with_profile")
    void createUserWithProfile(
            @Param("p_email") String email,
            @Param("p_username") String username,
            @Param("p_password_hash") String password,
            @Param("p_role") String role
    );
    @Modifying
    @Query("UPDATE User u SET u.imageUrl = :imageUrl WHERE u.id = :userId")
    int updateImageUrl(@Param("userId") Integer userId,
                       @Param("imageUrl") String imageUrl);
}