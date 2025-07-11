package com.scheduler.schedulerapp.service;

import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> checkCollision(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.findCollision(userId, startTime, endTime);
    }

    public Appointment createAppointment(Appointment appointment){
        List<Appointment> conflicts = checkCollision(
                appointment.getUserId(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Appointment conflicts with existing appointment: " + conflicts.get(0).getTitle());
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(String id, Appointment appointment) {

        List<Appointment> conflicts = checkCollision(
                appointment.getUserId(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        conflicts.removeIf(conflict -> conflict.getId().equals(id));

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Appointment conflicts with existing appointment: " + conflicts.get(0).getTitle());
        }

        appointment.setId(id);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByStartTimeBetween(start, end);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByUser(String userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public Optional<Appointment> getAppointmentById(String id) {
        return appointmentRepository.findById(id);
    }

    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }

    public void deleteMultipleAppointments(List<String> ids) {
        appointmentRepository.deleteAllById(ids);
    }
}
