package com.example.APPOINTMENT_SERVICE.Service;

import com.example.APPOINTMENT_SERVICE.FeignClient.VideoMeetingClient;
import com.example.APPOINTMENT_SERVICE.Model.Appointment;
import com.example.APPOINTMENT_SERVICE.Repository.Appointment_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class Appointment_service {

    @Autowired
    private Appointment_Repository appointmentRepo;

    @Autowired
    private VideoMeetingClient videoMeetingClient;


    // 1. EMPLOYEE ACTION: Nayi Appointment Request Bhejna (Direct Pending State me jayegi)
    public Appointment createAppointmentRequest(Long requesterId, Long targetId, String agenda, String priority, LocalDate preferredDate, String appointmentType) {
        Appointment appointment = new Appointment();
        appointment.setRequesterId(requesterId);
        appointment.setTargetId(targetId);
        appointment.setAgenda(agenda);
        appointment.setPriority(priority != null ? priority : "ROUTINE");
        appointment.setAppointmentDate(preferredDate);
        appointment.setAppointmentType(appointmentType!=null? appointmentType.toUpperCase():"VIRTUAL");
        appointment.setStatus("PENDING"); // Shuruat me status humesha PENDING rahega

        return appointmentRepo.save(appointment);
    }

    // 2. PA ACTION: Request APPROVE Karna (Timing aur Duration ke sath + Booking Clash Check)
    public Appointment approveAppointment(Long appointmentId, Long paId, LocalTime startTime, Integer durationMinutes, String locationRoom) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Error: Appointment request nahi mili!"));

        if (!"PENDING".equalsIgnoreCase(appointment.getStatus()) && !"RESCHEDULED".equalsIgnoreCase(appointment.getStatus())) {
            throw new RuntimeException("Error: Sirf PENDING ya RESCHEDULED appointments hi approve kiye ja sakte hain.");
        }

        // Double-Booking Check: Kya boss us time par pehle se kisi meeting me hain?
        boolean isBusy = appointmentRepo.isBossBusy(
                appointment.getTargetId(),
                appointment.getAppointmentDate(),
                startTime,
                durationMinutes
        );

        if (isBusy) {
            throw new RuntimeException("Error: Boss is busy! Is time slot par pehle se ek meeting approved hai.");
        }

        // Agar slot khali hai, toh appointment confirm karo
        appointment.setHandledById(paId);
        appointment.setStartTime(startTime);
        appointment.setDurationMinutes(durationMinutes);
        appointment.setStatus("APPROVED");
        appointment.setPaRemarks("Approved by PA. Slot confirmed.");
        appointment.setUpdatedAt(LocalDateTime.now());

        //agar meeting ofline ha to cabin/location do
        if("OFFLINE".equalsIgnoreCase(appointment.getAppointmentType()))
        {
            appointment.setLocationRoom(locationRoom!=null?locationRoom:"CEO Executive Cabin");
            appointment.setPaRemarks(("In-Person Meeting Approved.Location"+appointment.getLocationRoom()));
        }
        else {
            appointment.setPaRemarks("Virtual Meeting Approved.Video Room Link Generate.");
        }
        // 2. PEHLE DATABASE MEIN SAVE KARO (Commit changes)
        Appointment savedAppointment = appointmentRepo.save(appointment);

        // 3. DATABASE SAVE HONE KE BAAD FEIGN CLIENT CALL KARO
        // Yahan savedAppointment ka use karo taaki updated data hi jaye
        videoMeetingClient.createRoom(
                savedAppointment.getId(),
                savedAppointment.getTargetId(),
                savedAppointment.getRequesterId()
        );

        // 4. Aakhiri me saved object return karo
        return savedAppointment;
    }

    // 3. PA ACTION: Request REJECT Karna (Reason ke sath)
    public Appointment rejectAppointment(Long appointmentId, Long paId, String reason) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Error: Appointment request nahi mili!"));

        appointment.setHandledById(paId);
        appointment.setStatus("REJECTED");
        appointment.setPaRemarks(reason); // Kis wajah se reject kiya (e.g., "Falthu query", "Inappropriate channel")
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepo.save(appointment);
    }

    // 4. PA ACTION: RESCHEDULE Counter-Offer Dena
    public Appointment rescheduleAppointment(Long appointmentId, Long paId, LocalDate newDate, String alternativeRemark) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Error: Appointment request nahi mili!"));

        appointment.setHandledById(paId);
        appointment.setAppointmentDate(newDate);
        appointment.setStatus("RESCHEDULED");
        appointment.setPaRemarks(alternativeRemark); // e.g., "Sir is out of town, please look for next week."
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepo.save(appointment);
    }

    //5. FETCHING: PA/Boss ke liye saari Pending Requests dekhna
    public List<Appointment> getPendingRequestsForBoss(Long targetId) {
        return appointmentRepo.findByTargetIdAndStatusOrderByCreatedAtAsc(targetId, "PENDING");
    }

    // 6. FETCHING: Employee ke liye unki khud ki requests ka status dekhna
    public List<Appointment> getEmployeeAppointmentHistory(Long requesterId) {
        return appointmentRepo.findByRequesterIdOrderByCreatedAtDesc(requesterId);
    }
}
