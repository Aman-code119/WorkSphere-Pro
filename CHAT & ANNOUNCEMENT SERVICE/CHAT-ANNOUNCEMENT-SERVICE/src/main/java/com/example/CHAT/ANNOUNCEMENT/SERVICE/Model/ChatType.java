package com.example.CHAT.ANNOUNCEMENT.SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatType {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Column(name="chat_name",nullable = false)
        private String chatName;

        @Column(name="created_at",nullable = false, updatable = false)
        private LocalDateTime createdAt;

        // Clean Microservice Approach: No JOIN!
        // Isme us user ki ID aayegi jisne yeh chat room/group create kiya hai
        @Column(name = "created_by", nullable = false)
        private Long createdBy;

        @Column(name="chat_type",nullable = false)
        private String chatType; // Ismein store hoga: SELF, ONE_TO_ONE, GROUP, ya ANNOUNCEMENT

        @PrePersist
        protected void onCreate() {
                this.createdAt = LocalDateTime.now();
        }
}