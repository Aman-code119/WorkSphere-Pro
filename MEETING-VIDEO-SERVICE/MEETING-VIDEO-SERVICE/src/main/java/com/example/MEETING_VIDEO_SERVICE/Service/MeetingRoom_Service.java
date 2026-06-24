package com.example.MEETING_VIDEO_SERVICE.Service;

import com.example.MEETING_VIDEO_SERVICE.JWT_Utility.JwtVideo_Util;
import com.example.MEETING_VIDEO_SERVICE.Model.MeetingRoom;
import com.example.MEETING_VIDEO_SERVICE.Repository.MeetingRoom_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MeetingRoom_Service {

    @Autowired
    private MeetingRoom_Repository roomRepo;

    @Autowired
    private JwtVideo_Util jwtUtil;

    // 1. NEW ROOM CREATION: Jab meeting schedule karni ho
    public MeetingRoom initializeRoom(Long appointmentId, Long hostId, Long participantId) {
        // Check karo agar is appointment ka room pehle se toh nahi bana
        return roomRepo.findByAppointmentIdAndStatus(appointmentId, "ACTIVE")
                .orElseGet(() -> {
                    MeetingRoom room = new MeetingRoom();
                    room.setAppointmentId(appointmentId);
                    // Unique, un-guessable room name generation
                    room.setRoomName("MNC-ROOM-" + UUID.randomUUID().toString().substring(0, 8));
                    room.setHostId(hostId);
                    room.setParticipantId(participantId);
                    room.setStatus("ACTIVE");
                    return roomRepo.save(room);
                });
    }

    //2. TOKEN GENERATION: User ko safe entry access dena
    public String generateUserToken(Long appointmentId, Long userId, String userName, int durationMinutes) {
        MeetingRoom room = roomRepo.findByAppointmentIdAndStatus(appointmentId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Error: Is appointment ke liye koi active meeting room nahi mila!"));

        // Security check: Kya yeh user sach me is meeting ka part hai?
        if (!userId.equals(room.getHostId()) && !userId.equals(room.getParticipantId())) {
            throw new RuntimeException("Access Denied: Aap is secret meeting room ke liye authorized nahi hain!");
        }

        // Agar authorization paas ho gayi, toh token banakar bhej do
        return jwtUtil.generateSecureVideoToken(userId.toString(), userName, room.getRoomName(), durationMinutes);
    }

    // 3. CLOSE MEETING: Meeting khatam karne ke liye (Audit Trail tracking)
    public MeetingRoom concludeMeeting(Long appointmentId) {
        MeetingRoom room = roomRepo.findByAppointmentIdAndStatus(appointmentId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Error: Active room nahi mila."));

        room.setStatus("CONCLUDED");
        room.setConcludedAt(LocalDateTime.now());
        return roomRepo.save(room);
    }
}