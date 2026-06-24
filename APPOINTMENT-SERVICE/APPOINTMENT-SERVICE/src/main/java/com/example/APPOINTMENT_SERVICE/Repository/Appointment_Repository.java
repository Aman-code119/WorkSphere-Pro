package com.example.APPOINTMENT_SERVICE.Repository;

import com.example.APPOINTMENT_SERVICE.Model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface Appointment_Repository extends JpaRepository<Appointment,Long> {


    // 1. Employee ke liye: Apne bhejey hue saare appointments dekhna (Inbox/History)
    List<Appointment> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);

    // 2.PA/Secretary ke liye: Boss ke saare 'PENDING' requests nikalna taaki wo action le sake
    List<Appointment> findByTargetIdAndStatusOrderByCreatedAtAsc(Long targetId, String status);

    // 3.High-Rank Boss ke liye: Aaj ke saare APPROVED appointments (Schedule View)
    List<Appointment> findByTargetIdAndAppointmentDateAndStatusOrderByStartTimeAsc(Long targetId, LocalDate date, String status);

    // 4.Double-Booking Protection: Check karna ki kya us date aur time par boss pehle se busy toh nahi hain
    // Yeh query check karegi ki naya slot kisi purane approved slot ke beech me toh nahi aa raha
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.targetId = :targetId " +
            "AND a.appointmentDate = :date AND a.status = 'APPROVED' " +
            "AND ((a.startTime <= :startTime AND FUNCTION('ADDTIME', a.startTime, SEC_TO_TIME(a.durationMinutes * 60)) > :startTime) " +
            "OR (:startTime <= a.startTime AND FUNCTION('ADDTIME', :startTime, SEC_TO_TIME(:duration * 60)) > a.startTime))")
    boolean isBossBusy(@Param("targetId") Long targetId,
                       @Param("date") LocalDate date,
                       @Param("startTime") LocalTime startTime,
                       @Param("duration") Integer duration);
}
