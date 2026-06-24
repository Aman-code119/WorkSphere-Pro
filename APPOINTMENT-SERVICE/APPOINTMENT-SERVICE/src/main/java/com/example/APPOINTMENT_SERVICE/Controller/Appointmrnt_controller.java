package com.example.APPOINTMENT_SERVICE.Controller;

import com.example.APPOINTMENT_SERVICE.Model.Appointment;
import com.example.APPOINTMENT_SERVICE.Service.Appointment_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointment")
public class Appointmrnt_controller {

    @Autowired
    private Appointment_service appointmentService;

    // 1. EMPLOYEE ENDPOINT: Nayi Appointment Request Bhejna
    // URL: POST http://localhost:8083/api/appointments/request
    @PostMapping("/request")
    public ResponseEntity<?> createRequest(@RequestParam Long requesterId,
                                           @RequestParam Long targetId,
                                           @RequestParam String agenda,
                                           @RequestParam(required = false) String priority,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate preferredDate, String appointmentType) {
        try {
            Appointment appointment = appointmentService.createAppointmentRequest(requesterId, targetId, agenda, priority, preferredDate, appointmentType);
            return ResponseEntity.ok(Map.of(
                    "message", "Appointment request successfully submitted to PA!",
                    "appointmentId", appointment.getId(),
                    "status", appointment.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. PA ENDPOINT: Request APPROVE Karna (Slot Allocation + Clash Check)
    // URL: POST http://localhost:8083/api/appointments/approve
    @PostMapping("/approve")
    public ResponseEntity<?> approveRequest(@RequestParam Long appointmentId,
                                            @RequestParam Long paId,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                                            @RequestParam Integer durationMinutes, String locationRoom) {
        try {
            Appointment approved = appointmentService.approveAppointment(appointmentId, paId, startTime, durationMinutes, locationRoom);
            return ResponseEntity.ok(Map.of(
                    "message", "Appointment APPROVED successfully!",
                    "status", approved.getStatus(),
                    "allocatedTime", approved.getStartTime(),
                    "duration", approved.getDurationMinutes() + " Minutes"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. PA ENDPOINT: Request REJECT Karna
    // URL: POST http://localhost:8083/api/appointments/reject
    @PostMapping("/reject")
    public ResponseEntity<?> rejectRequest(@RequestParam Long appointmentId,
                                           @RequestParam Long paId,
                                           @RequestParam String reason) {
        try {
            Appointment rejected = appointmentService.rejectAppointment(appointmentId, paId, reason);
            return ResponseEntity.ok(Map.of(
                    "message", "Appointment REJECTED by PA.",
                    "status", rejected.getStatus(),
                    "remarks", rejected.getPaRemarks()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. PA ENDPOINT: Request RESCHEDULE Karna (Counter-Offer)
    // URL: POST http://localhost:8083/api/appointments/reschedule
    @PostMapping("/reschedule")
    public ResponseEntity<?> rescheduleRequest(@RequestParam Long appointmentId,
                                               @RequestParam Long paId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
                                               @RequestParam String remark) {
        try {
            Appointment rescheduled = appointmentService.rescheduleAppointment(appointmentId, paId, newDate, remark);
            return ResponseEntity.ok(Map.of(
                    "message", "Appointment rescheduled offer sent to employee.",
                    "status", rescheduled.getStatus(),
                    "newProposedDate", rescheduled.getAppointmentDate()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. PA/BOSS ENDPOINT: Saari Pending Requests Dekhna
    // URL: GET http://localhost:8083/api/appointments/pending?targetId=2
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(@RequestParam Long targetId) {
        try {
            List<Appointment> pendingList = appointmentService.getPendingRequestsForBoss(targetId);
            return ResponseEntity.ok(pendingList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 6. EMPLOYEE ENDPOINT: Apni Bhejey Hue Requests Ka Status/History Dekhna
    // URL: GET http://localhost:8083/api/appointments/history?requesterId=101
    @GetMapping("/history")
    public ResponseEntity<?> getEmployeeHistory(@RequestParam Long requesterId) {
        try {
            List<Appointment> historyList = appointmentService.getEmployeeAppointmentHistory(requesterId);
            return ResponseEntity.ok(historyList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
