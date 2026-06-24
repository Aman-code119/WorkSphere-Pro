package com.example.AUTH.EMPLOYEE.SERVICE.Controller;

import com.example.AUTH.EMPLOYEE.SERVICE.DTO.loginDTO;
import com.example.AUTH.EMPLOYEE.SERVICE.DTO.registerDTO;
import com.example.AUTH.EMPLOYEE.SERVICE.Model.*;
import com.example.AUTH.EMPLOYEE.SERVICE.Repository.*;
import com.example.AUTH.EMPLOYEE.SERVICE.Service.userService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private userService userservice;

    @GetMapping("/users/{id}/exists")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userservice.checkUserExists(id)); // Agar user hai to true bhejega, nahi to false
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginEmployee(@RequestBody loginDTO loginDto, HttpServletRequest request) {
        try {

            String ipAddress=request.getHeader("X-Forwarded-For");
            if(ipAddress==null||ipAddress.isEmpty()) {
                ipAddress=request.getRemoteAddr();
            }
            String userAgent=request.getHeader("User-Agent");

            USER user = userservice.loginEmployee(loginDto,ipAddress,userAgent);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/registered")
    public ResponseEntity<?> register(@Valid @RequestBody registerDTO regData) {
        try {
            USER user = new USER();
            user.setUsername(regData.getUsername());
            user.setEmail(regData.getEmail());
            user.setPassword(regData.getPassword());

            USER savedUser =userservice.registerUser(user,regData.getDepartmentId(),regData.getRoleId());
            return ResponseEntity.ok(savedUser);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{id}/role")
    public ResponseEntity<String> getUserRoleById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(userservice.getUserRoleById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userservice.getEmployeeById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<List<USER>> getAllEmployees() {
        return ResponseEntity.ok(userservice.getAllEmployees());
    }
}