package com.example.CHAT.ANNOUNCEMENT.SERVICE.Repository;

import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.AnnouncementReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementReadStatus_Repository extends JpaRepository<AnnouncementReadStatus,Long> {

    boolean existsByUserIdAndAnnouncementId(Long userId, Long announcementId);

    Long countByUserIdAndAnnouncementIdIn(Long userId, List<Long> allAccessibleAnnouncementIds);
}
