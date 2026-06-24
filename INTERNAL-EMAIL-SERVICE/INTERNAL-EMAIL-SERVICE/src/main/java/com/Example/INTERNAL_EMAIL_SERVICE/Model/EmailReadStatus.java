package com.Example.INTERNAL_EMAIL_SERVICE.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="email_read_status")
public class EmailReadStatus {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long emailId;  // Kis email ki baat ho rahi hai
        private Long userId;   // Kis employee ne padha

        private boolean isRead = false; // Default false rahega

        private LocalDateTime readAt;   // Kis time par email kholi (Padhi)
}