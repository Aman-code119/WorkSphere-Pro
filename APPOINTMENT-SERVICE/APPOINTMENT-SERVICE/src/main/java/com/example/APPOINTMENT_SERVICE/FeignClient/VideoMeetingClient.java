package com.example.APPOINTMENT_SERVICE.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "MEETING-VIDEO-SERVICE")
public interface VideoMeetingClient {

        @PostMapping("/init")
        void createRoom(@RequestParam("appointmentId") Long appointmentId,
                        @RequestParam("hostId") Long hostId,
                        @RequestParam("participantId") Long participantId);
}