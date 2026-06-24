package com.example.AUTH.EMPLOYEE.SERVICE.Repository;

import com.example.AUTH.EMPLOYEE.SERVICE.Model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLog_Repository  extends JpaRepository<LoginLog,Long> {


        // Yeh dhoondega ki kya is user ne is IP se pehle kabhi login kiya hai?
        List<LoginLog> findByUserIdAndIpAddress(Long userId, String ipAddress);
}