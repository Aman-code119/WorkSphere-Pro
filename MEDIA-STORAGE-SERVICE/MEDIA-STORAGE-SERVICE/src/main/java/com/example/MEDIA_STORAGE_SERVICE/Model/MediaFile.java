package com.example.MEDIA_STORAGE_SERVICE.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="media_file")
public class MediaFile {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Kis meeting ya appointment se judi hai yeh file
        @Column(nullable = false)
        private Long referenceId;

        // File ka type kya hai (e.g., "MEETING_RECORDING", "PRESENTATION_PDF", "AUDIT_LOG")
        @Column(nullable = false)
        private String fileType;

        //Asli file ka naam (e.g., "Q2_Review_Meeting.mp4")
        private String fileName;

        // Storage path ya server URL jahan file save hui hai (e.g., "/storage/uploads/videos/xyz.mp4")
        @Column(nullable = false)
        private String fileStorageUrl;

        // File size bytes me tracking ke liye
        private Long fileSize;

        // Logs tracking
        private LocalDateTime uploadedAt = LocalDateTime.now();
}