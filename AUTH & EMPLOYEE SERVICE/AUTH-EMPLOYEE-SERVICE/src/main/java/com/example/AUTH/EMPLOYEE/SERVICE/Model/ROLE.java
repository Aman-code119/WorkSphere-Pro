package com.example.AUTH.EMPLOYEE.SERVICE.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ROLE {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String roleName; // Isme aayega: 'EMPLOYEE', 'HR', 'MANAGER', 'CEO'
}