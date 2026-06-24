package com.example.AUTH.EMPLOYEE.SERVICE.Service;

import com.example.AUTH.EMPLOYEE.SERVICE.DTO.loginDTO;
import com.example.AUTH.EMPLOYEE.SERVICE.Model.*;
import com.example.AUTH.EMPLOYEE.SERVICE.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class userService {

    @Autowired
    private User_Repository userrepo;

    @Autowired
    private Department_Repository departmentrepo;

    @Autowired
    private ROLE_Repository rolerepo;

    @Autowired
    private LoginLog_Repository loginlogrepo;

    @Autowired
    private Notification_Repository notificationrepo;

    //1. register user
    public USER registerUser(USER user, Long departmentId, Long roleId) {

        Optional<USER> existingEmail = userrepo.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("Error: Email already exists!");
        }

        // 2. Check karo kya Username pehle se database me hai?
        Optional<USER> existingUsername = userrepo.findByUsername(user.getUsername());
        if (existingUsername.isPresent()) {
            throw new RuntimeException("Error: Username already taken!");
        }

        Department dept = departmentrepo.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Error: WRONG Department ID!"));

        ROLE role = rolerepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Error: WRONG Role ID!"));

        user.setDepartment(dept);
        user.setRole(role);

        // 3. Sab sahi hai, ab direct database me poora object save kar do
        return userrepo.save(user);
    }

    // 2. LOGIN LOGIC (Saara device checking ab yahan aa gaya)
    public USER loginEmployee(loginDTO loginDto, String ipAddress, String userAgent) {
        USER user = userrepo.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: invalid USERNAME!"));

        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new RuntimeException("Error: WRONG password!");
        }

        // Check new IP for alert
        var existingLogs = loginlogrepo.findByUserIdAndIpAddress(user.getId(), ipAddress);
        if (existingLogs.isEmpty()) {
            Notification loginAlert = new Notification();
            loginAlert.setUserId(user.getId());
            loginAlert.setTitle("New Login Detected");
            loginAlert.setMessage("Your account was accessed from a new device/IP: " + ipAddress + " using " + userAgent);
            loginAlert.setCreatedAt(LocalDateTime.now());
            loginAlert.setRead(false);
            notificationrepo.save(loginAlert);
        }

        // Save Log
        LoginLog currentLog = new LoginLog();
        currentLog.setUserId(user.getId());
        currentLog.setEmail(user.getEmail());
        currentLog.setIpAddress(ipAddress);
        currentLog.setDeviceDetails(userAgent);
        currentLog.setLoginTime(LocalDateTime.now());
        loginlogrepo.save(currentLog);

        return user;
    }

    // 3. OTHER UTILITY METHODS
    public boolean checkUserExists(Long id) {
        return userrepo.existsById(id);
    }

    public String getUserRoleById(Long id) {
        USER user = userrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not found!"));
        return user.getRole().getRoleName();
    }

    public USER getEmployeeById(Long id) {
        USER user = userrepo.getUserById(id);
        if (user == null) {
            throw new RuntimeException("Employee nahi mila!");
        }
        return user;
    }

    public List<USER> getAllEmployees() {
        return userrepo.getAllUsers();
    }
}