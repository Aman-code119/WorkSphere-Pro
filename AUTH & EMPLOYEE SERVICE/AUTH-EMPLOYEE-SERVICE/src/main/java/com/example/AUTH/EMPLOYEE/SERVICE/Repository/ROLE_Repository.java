package com.example.AUTH.EMPLOYEE.SERVICE.Repository;

import com.example.AUTH.EMPLOYEE.SERVICE.Model.ROLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ROLE_Repository extends JpaRepository<ROLE,Long> {
}
