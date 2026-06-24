package com.example.MEETING_VIDEO_SERVICE.Controller;

import com.example.MEETING_VIDEO_SERVICE.Model.MeetingRoom;
import com.example.MEETING_VIDEO_SERVICE.Service.MeetingRoom_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/video-meeting")
public class MeetingRoom_controller {


    @Autowired
    private MeetingRoom_Service meetingService;

        // 1. ENDPOINT: Naya Room Initialize karna (Usually called internally by APPOINTMENT-SERVICE)
        // URL: POST http://localhost:8084/api/video-meetings/init
        @PostMapping("/init")
        public ResponseEntity<?> createRoom(@RequestParam Long appointmentId,
                                            @RequestParam Long hostId,
                                            @RequestParam Long participantId) {
            try {
                MeetingRoom room = meetingService.initializeRoom(appointmentId, hostId, participantId);
                return ResponseEntity.ok(Map.of(
                        "message", "Secure Room initialized successfully!",
                        "roomName", room.getRoomName(),
                        "status", room.getStatus()
                ));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // 2. ENDPOINT: Join karne se pehle Token request karna
        // URL: POST http://localhost:8084/api/video-meetings/join-token
        @PostMapping("/join-token")
        public ResponseEntity<?> joinRoom(@RequestParam Long appointmentId,
                                          @RequestParam Long userId,
                                          @RequestParam String userName,
                                          @RequestParam(defaultValue = "30") int durationMinutes) {
            try {
                String token = meetingService.generateUserToken(appointmentId, userId, userName, durationMinutes);
                return ResponseEntity.ok(Map.of(
                        "message", "Access Token granted. Security Cleared!",
                        "secureToken", token
                ));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // 3. ENDPOINT: Meeting Khatam karna
        // URL: POST http://localhost:8084/api/video-meetings/close
        @PostMapping("/close")
        public ResponseEntity<?> closeRoom(@RequestParam Long appointmentId) {
            try {
                MeetingRoom room = meetingService.concludeMeeting(appointmentId);
                return ResponseEntity.ok(Map.of(
                        "message", "Meeting closed and logged safely.",
                        "status", room.getStatus(),
                        "concludedAt", room.getConcludedAt()
                ));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }
}