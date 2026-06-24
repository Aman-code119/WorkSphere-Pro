package com.Example.INTERNAL_EMAIL_SERVICE.Repository;

import com.Example.INTERNAL_EMAIL_SERVICE.Model.EmailReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailReadStatus_Repository extends JpaRepository<EmailReadStatus,Long> {

    // 1. Check karne ke liye ki kya is user ne ye email pehle padhi hai
    Optional<EmailReadStatus> findByEmailIdAndUserId(Long emailId, Long userId);

    // 2. Kisi user ne jitni emails padh li hain, unki Email IDs nikalne ke liye
    @Query("SELECT r.emailId FROM EmailReadStatus r WHERE r.userId = :userId AND r.isRead = true")
    List<Long> findReadEmailIdsByUserId(@Param("userId") Long userId);
}
