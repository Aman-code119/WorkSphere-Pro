package com.example.AUTH.EMPLOYEE.SERVICE.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class loginDTO {
        @NotBlank(message="username NOT empty!")
        @Column(nullable = false,unique = true)
        private String username;

        @NotBlank(message = "Password NOT Empty!")
        @Column(unique = true,nullable = false)
        private String password;
}