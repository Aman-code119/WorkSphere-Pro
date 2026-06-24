package com.Example.INTERNAL_EMAIL_SERVICE.FeignClientIntegration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name ="AUTH-EMPLOYEE-SERVICE")
public interface AuthServiceClient {

    // 1. Pure department ke employees ki IDs uthane ke liye
    @GetMapping("/api/users/by-department")
    List<Long> getUserIdsByDepartment(@RequestParam("deptName") String deptName);

    // 2. Company ke saare active employees ki IDs uthane ke liye (For "ALL" selection)
    @GetMapping("/api/users/all-ids")
    List<Long> getAllActiveUserIds();

}
