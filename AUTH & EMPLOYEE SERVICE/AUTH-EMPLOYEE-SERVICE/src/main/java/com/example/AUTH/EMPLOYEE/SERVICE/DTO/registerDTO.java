package com.example.AUTH.EMPLOYEE.SERVICE.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class registerDTO {

        @NotBlank(message = "Name NOT EMPTY!")
        private String username;

        @NotBlank(message = "Email NOT EMPTY!")
        @Email(message = "Enter correct mail format")
        private String email;

        @NotBlank(message = "Password Compulsory")
        private String password;

        @NotBlank(message="DepartmentID NOT empty!")
        private Long departmentId;

        @NotBlank(message = "RoleID NOT empty!")
        private Long roleId;
}