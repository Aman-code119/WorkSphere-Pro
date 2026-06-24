package com.example.MEETING_VIDEO_SERVICE.Repository;

import com.example.MEETING_VIDEO_SERVICE.Model.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingRoom_Repository extends JpaRepository<MeetingRoom,Long> {

    Optional<MeetingRoom> findByAppointmentIdAndStatus(Long appointmentId, String active);
}
