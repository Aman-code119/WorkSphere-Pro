package com.example.CHAT.ANNOUNCEMENT.SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private Long senderId; // HR ya Admin ki ID
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    // Ek Announcement bohot saare groups me select ho sakti h
    @ManyToMany
    @JoinTable(
            name = "announcement_groups",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<ChatType> targetGroups;
}
