package com.Example.INTERNAL_EMAIL_SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "internal_emails")
@AllArgsConstructor
@NoArgsConstructor
public class Internal_Emails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId; // CEO, HR ya Admin ki ID

    @Column(columnDefinition = "TEXT")
    private String recipientIds; // Background query se nikli saari IDs ka JSON Array: "[101,102,105]"

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    private LocalDateTime sentAt;

    private boolean isConvertedToChat = false;

    // Soft Delete Columns (WhatsApp Feature ke liye)
    private boolean isDeletedBySender = false; // True hote hi sabke liye "This message was deleted" ho jayega

    // 📎 Attachments Support
    @ElementCollection
    @CollectionTable(name = "email_attachments", joinColumns = @JoinColumn(name = "email_id"))
    @Column(name = "file_path")
    private List<String> attachmentFilePaths;


    private Long parentEmailId; // Purani email ki ID (Thread tracking ke liye)

    private String emailType = "FRESH"; // FRESH, REPLY, ya FORWARD

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}