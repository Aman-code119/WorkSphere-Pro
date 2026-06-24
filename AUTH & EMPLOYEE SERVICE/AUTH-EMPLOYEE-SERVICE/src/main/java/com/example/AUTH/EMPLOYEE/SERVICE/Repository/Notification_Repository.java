package com.example.AUTH.EMPLOYEE.SERVICE.Repository;

import com.example.AUTH.EMPLOYEE.SERVICE.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Notification_Repository extends JpaRepository<Notification,Long> {


        // Employee ke inbox me sirf uski notifications reverse order (latest first) me dikhane ke liye
        List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}