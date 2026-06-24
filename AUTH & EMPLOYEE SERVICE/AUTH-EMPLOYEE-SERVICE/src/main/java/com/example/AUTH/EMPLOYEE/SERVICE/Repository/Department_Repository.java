package com.example.AUTH.EMPLOYEE.SERVICE.Repository;

import com.example.AUTH.EMPLOYEE.SERVICE.Model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Department_Repository extends JpaRepository<Department,Long> {
}
