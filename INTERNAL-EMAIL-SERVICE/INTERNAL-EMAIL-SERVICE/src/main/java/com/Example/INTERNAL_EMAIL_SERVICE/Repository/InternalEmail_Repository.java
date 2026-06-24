package com.Example.INTERNAL_EMAIL_SERVICE.Repository;

import com.Example.INTERNAL_EMAIL_SERVICE.Model.Internal_Emails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalEmail_Repository extends JpaRepository<Internal_Emails ,Long> {


    // 💡 Yeh query exact 'recipient_ids' column me user ki ID dhoond legi bina crash kiye
    @Query(value = "SELECT * FROM internal_emails WHERE recipient_ids LIKE %:userId%", nativeQuery = true)
    List<Internal_Emails> findByRecipientIdCustom(@Param("userId") String userId);

    List<Internal_Emails> findBySenderIdOrderBySentAtDesc(Long senderId);
}
