package com.example.AUTH.EMPLOYEE.SERVICE.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginLog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long userId;
        private String email;
        private String ipAddress;
        private String deviceDetails; // Browser aur OS ka naam
        private LocalDateTime loginTime;
}