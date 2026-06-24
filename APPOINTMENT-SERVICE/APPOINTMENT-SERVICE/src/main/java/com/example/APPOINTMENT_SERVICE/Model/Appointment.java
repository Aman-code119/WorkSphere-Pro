package com.example.APPOINTMENT_SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name="appointment")
public class Appointment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Request bhejnewale employee ki ID
        private Long requesterId;

        // Jis bade rank ke employee se milna hai uski ID
        private Long targetId;

        // Jis PA/Secretary ne isko handle kiya uski ID
        private Long handledById;

        // Milne ka kaaran (Query ya Suggestion)
        @Column(columnDefinition = "TEXT", nullable = false)
        private String agenda;

        // Priority: ROUTINE, URGENT, CRITICAL
        private String priority = "ROUTINE";

        // Kis tareekh ko appointment chahiye
        private LocalDate appointmentDate;

        // Kitne baje ka appointment mila hai
        private LocalTime startTime;

        // Kitni der ki meeting hai (Minutes mein - e.g., 15, 30, 45)
        private Integer durationMinutes;

        // Current State: PENDING, APPROVED, REJECTED, RESCHEDULED
        private String status = "PENDING";

        // Agar PA ne reject ya reschedule kiya toh uska reason/comment
        private String paRemarks;

        //  Logs tracking ke liye timestamps
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt;

        private String appointmentType; // "VIRTUAL" ya "OFFLINE"
        private String locationRoom;     // Agar OFFLINE hai toh "Cabin-4" ya "Conference Hall-1"
}