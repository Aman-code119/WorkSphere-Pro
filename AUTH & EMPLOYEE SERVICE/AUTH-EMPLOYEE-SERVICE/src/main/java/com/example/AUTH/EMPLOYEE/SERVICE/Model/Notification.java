package com.example.AUTH.EMPLOYEE.SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;        // Kis employee ko alert dikhana hai
    private String title;       // Example: "Security Alert: New Login"

    @Column(length = 500)
    private String message;     // Example: "Apne 14-06-2026 ko 16:15 par login kiya hai."

    private LocalDateTime createdAt;
    private boolean isRead;    // Kya employee ne ye alert dekh liya hai? (True/False)
}
