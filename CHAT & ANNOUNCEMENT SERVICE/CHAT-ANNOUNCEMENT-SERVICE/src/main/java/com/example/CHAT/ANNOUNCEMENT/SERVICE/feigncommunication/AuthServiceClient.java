package com.example.CHAT.ANNOUNCEMENT.SERVICE.feigncommunication;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AUTH-EMPLOYEE-SERVICE")
public interface AuthServiceClient {


        // Yeh Auth-MS ke endpoint ko hit karke role/rank return karega
        @GetMapping("/api/auth/users/{id}/role")
        String getUserRoleById(@PathVariable("id") Long id);

        //yeh Auth-MS ma ja kr check kraga ki user valid h ya nhi
        @GetMapping("/api/auth/users/{id}/exists")
        Boolean checkUserExists(@PathVariable("id") Long id);
}