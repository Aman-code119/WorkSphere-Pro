package com.example.CHAT.ANNOUNCEMENT.SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "announcement_read_status")
public class AnnouncementReadStatus {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Long userId;        // Employee ki ID
        private Long announcementId;   // Kaunsi announcement hai
        private boolean isRead = true; // Agar entry is table me hai to matlab read ho gayi
        private LocalDateTime readAt = LocalDateTime.now();
}