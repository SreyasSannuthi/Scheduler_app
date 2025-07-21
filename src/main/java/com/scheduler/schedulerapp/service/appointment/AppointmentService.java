package com.scheduler.schedulerapp.service.appointment;

import com.scheduler.schedulerapp.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    Appointment createAppointment(Appointment appointment);
    Appointment updateAppointment(String id, Appointment appointment);
    Optional<Appointment> getAppointmentById(String id);
    void deleteAppointment(String id);
    void deleteMultipleAppointments(List<String> ids);

    List<Appointment> getAllAppointments();
    List<Appointment> getAppointmentsByDoctor(String doctorId);
    List<Appointment> getAppointmentsByPatient(String patientId);
    List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);
    List<Appointment> getAppointmentsByDoctorAndDateRange(String doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> getAppointmentsByPatientAndDateRange(String patientId, LocalDateTime start, LocalDateTime end);
    List<Appointment> getAppointmentsByStatus(String status);

    List<Appointment> checkCollision(String doctorId,String patientId,LocalDateTime startTime, LocalDateTime endTime);
}