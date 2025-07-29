package com.scheduler.schedulerapp.service.appointment;

import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appointment> checkCollision(String doctorId, String patientId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Appointment> conflicts = new ArrayList<>();

        List<Appointment> doctorConflicts = appointmentRepository.findDoctorCollision(doctorId, startTime, endTime);
        conflicts.addAll(doctorConflicts);

        List<Appointment> patientConflicts = appointmentRepository.findPatientCollision(patientId, startTime, endTime);
        conflicts.addAll(patientConflicts);

        return conflicts;
    }

    public Appointment createAppointment(Appointment appointment) {

        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        List<Appointment> conflicts = checkCollision(
                appointment.getDoctorId(),
                appointment.getPatientId(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Appointment conflicts with existing appointment: " + conflicts.get(0).getTitle());
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(String id, Appointment appointment) {

        appointment.setUpdatedAt(LocalDateTime.now());

        List<Appointment> conflicts = checkCollision(
                appointment.getDoctorId(),
                appointment.getPatientId(),
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

    public List<Appointment> getAppointmentsByDoctorAndDateRange(String doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByDoctorIdAndStartTimeBetween(doctorId, start, end);
    }

    public List<Appointment> getAppointmentsByPatientAndDateRange(String patientId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByPatientIdAndStartTimeBetween(patientId, start, end);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId);
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


    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    public List<Appointment> getAppointmentsByBranch(String branchId) {
        return appointmentRepository.findByBranchId(branchId);
    }

    public List<Appointment> getAppointmentsByBranchAndDateRange(String branchId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByBranchIdAndStartTimeBetween(branchId, start, end);
    }
}
