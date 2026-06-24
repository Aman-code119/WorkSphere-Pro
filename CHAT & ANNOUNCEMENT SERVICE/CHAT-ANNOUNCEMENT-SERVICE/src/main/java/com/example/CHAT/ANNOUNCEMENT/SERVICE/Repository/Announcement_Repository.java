package com.example.CHAT.ANNOUNCEMENT.SERVICE.Repository;

import com.example.CHAT.ANNOUNCEMENT.SERVICE.Model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Announcement_Repository extends JpaRepository<Announcement,Long> {

    @Query(value = "SELECT a.announcement_id FROM announcement_target_groups a WHERE a.group_id IN (:userGroupIds)", nativeQuery = true)
    List<Long> findAnnouncementIdsByGroupIds(List<Long> userGroupIds);
}
