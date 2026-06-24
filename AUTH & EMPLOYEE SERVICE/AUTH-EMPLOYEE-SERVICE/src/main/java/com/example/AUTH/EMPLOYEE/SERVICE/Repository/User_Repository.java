package com.example.AUTH.EMPLOYEE.SERVICE.Repository;

import com.example.AUTH.EMPLOYEE.SERVICE.Model.USER;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface User_Repository extends JpaRepository<USER,Long> {

    Optional<USER> findByUsername(@NotBlank(message="username NOT empty!") String username);

    // 🚨 FIX 1: HIBERNATE KO BATAO KI SARE USERS NIKALNE HAIN (JPQL QUERY)
    @Query("SELECT u FROM USER u")
    List<USER> getAllUsers();

    // 🚨 FIX 2: PARAMETER BINDING KE SATH SPECIFIC USER NIKALNA
    @Query("SELECT u FROM USER u WHERE u.id = :id")
    USER getUserById(@Param("id") Long id);

    Optional<USER> findByEmail(String email);
}
