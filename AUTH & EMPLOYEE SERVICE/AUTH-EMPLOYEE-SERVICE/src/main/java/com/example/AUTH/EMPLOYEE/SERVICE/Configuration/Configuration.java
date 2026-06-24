package com.example.AUTH.EMPLOYEE.SERVICE.Configuration;

import com.example.AUTH.EMPLOYEE.SERVICE.Model.Department;
import com.example.AUTH.EMPLOYEE.SERVICE.Model.ROLE;
import com.example.AUTH.EMPLOYEE.SERVICE.Repository.Department_Repository;
import com.example.AUTH.EMPLOYEE.SERVICE.Repository.ROLE_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Configuration implements CommandLineRunner {

    @Autowired
    private ROLE_Repository roleRepository;

    @Autowired
    private Department_Repository departmentRepository;

        @Override
        public void run(String... args) throws Exception {

            // 1. ROLES INITIALIZE KARNA
            if (roleRepository.count() == 0) { // Agar table khali hai tabhi chalega
                roleRepository.save(new ROLE(null, "EMPLOYEE"));
                roleRepository.save(new ROLE(null, "HR"));
                roleRepository.save(new ROLE(null, "MANAGER"));
                roleRepository.save(new ROLE(null, "CEO"));
            }

            // 2. DEPARTMENTS INITIALIZE KARNA
            if (departmentRepository.count() == 0) { // Agar table khali hai tabhi chalega
                departmentRepository.save(new Department(null, "Dev Engine"));
                departmentRepository.save(new Department(null, "HR Department"));
                departmentRepository.save(new Department(null, "Sales & Marketing"));
                departmentRepository.save(new Department(null, "Finance"));
            }
        }
}