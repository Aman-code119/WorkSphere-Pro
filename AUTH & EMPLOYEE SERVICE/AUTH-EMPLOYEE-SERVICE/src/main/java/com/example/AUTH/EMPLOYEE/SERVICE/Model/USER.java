package com.example.AUTH.EMPLOYEE.SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class USER {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false,unique = true)
        private String username;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "role_id", nullable = false)
        private ROLE role;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "department_id",nullable = false)
        private Department department;
}