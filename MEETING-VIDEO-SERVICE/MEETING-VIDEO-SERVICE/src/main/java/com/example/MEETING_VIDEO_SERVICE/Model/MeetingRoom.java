package com.example.MEETING_VIDEO_SERVICE.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Data
@Table(name="meeting_room")
public class MeetingRoom {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private Long appointmentId;

        @Column(nullable = false, unique = true)
        private String roomName;

        private Long hostId;        // High-rank employee / Boss
        private Long participantId; // Low-rank employee

        private String status = "ACTIVE"; // ACTIVE, CONCLUDED

        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime concludedAt;
}