package com.scheduler.schedulerapp.service.doctor;

import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.StaffBranchMapping;
import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.DoctorBranchMappingRepository;
import com.scheduler.schedulerapp.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorBranchMappingRepository doctorBranchMappingRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public List<HospitalStaff> getAllDoctors() {
        return doctorRepository.findAllWhoIsActive();
    }

    @Override
    public Optional<HospitalStaff> getDoctorById(String id) {
        return doctorRepository.findById(id);
    }

    @Override
    public List<HospitalStaff> getDoctorsByRole(String role) {
        return doctorRepository.findByRole(role);
    }

    @Override
    public HospitalStaff createDoctor(HospitalStaff doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }

        doctor.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
        doctor.setEndDate("");
        doctor.setIsActive(true);
        return doctorRepository.save(doctor);
    }

    @Override
    public HospitalStaff updateDoctor(String id, HospitalStaff doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }

        Optional<HospitalStaff> existingDoctorOpt = doctorRepository.findById(id);
        if (existingDoctorOpt.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + id);
        }

        HospitalStaff existingDoctor = existingDoctorOpt.get();

        if (doctor.getIsActive() != null && !doctor.getIsActive().equals(existingDoctor.getIsActive())) {
            if (!doctor.getIsActive()) {
                doctor.setEndDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
                removeDoctorMappingsAndAppointments(id);
                System.out.println("Doctor " + doctor.getName() + " deactivated at: " + doctor.getEndDate());
            } else {
                doctor.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
                doctor.setEndDate("");
                System.out.println("Doctor " + doctor.getName() + " reactivated. Previous end date preserved: " + existingDoctor.getEndDate());
            }
        }

        doctor.setId(id);
        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(String id) {
        Optional<HospitalStaff> doctorCheck = doctorRepository.findById(id);
        if (doctorCheck.isPresent()) {
            HospitalStaff doctor = doctorCheck.get();

            doctor.setIsActive(false);
            doctor.setEndDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));

            removeDoctorMappingsAndAppointments(id);

            doctorRepository.save(doctor);

            System.out.println("Doctor " + doctor.getName() + " has been deactivated and all mappings/appointments handled");
        } else {
            throw new IllegalArgumentException("Doctor not found with ID: " + id);
        }
    }

    @Transactional
    private void removeDoctorMappingsAndAppointments(String doctorId) {
        try {
            removeDoctorBranchMappings(doctorId);

            handleDoctorAppointments(doctorId);

        } catch (Exception e) {
            System.err.println("Error handling doctor deactivation for doctor " + doctorId + ": " + e.getMessage());
            throw new RuntimeException("Failed to handle doctor deactivation: " + e.getMessage());
        }
    }

    private void removeDoctorBranchMappings(String doctorId) {
        try {
            List<StaffBranchMapping> mappings = doctorBranchMappingRepository.findByDoctorId(doctorId);

            if (!mappings.isEmpty()) {
                doctorBranchMappingRepository.deleteAll(mappings);
                System.out.println("Removed " + mappings.size() + " branch mappings for doctor ID: " + doctorId);
            }
        } catch (Exception e) {
            System.err.println("Error removing doctor mappings for doctor " + doctorId + ": " + e.getMessage());
            throw new RuntimeException("Failed to remove doctor mappings: " + e.getMessage());
        }
    }

 void handleDoctorAppointments(String doctorId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);

            if (!appointments.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                int cancelledCount = 0;
                int preservedCount = 0;

                for (Appointment appointment : appointments) {
                    if (appointment.getStartTime().isAfter(now) &&
                            "scheduled".equals(appointment.getStatus())) {
                        appointment.setStatus("cancelled");
                        appointment.setUpdatedAt(LocalDateTime.now());
                        appointmentRepository.save(appointment);
                        cancelledCount++;
                    } else {
                        preservedCount++;
                    }
                }

                System.out.println("Doctor appointments handled - Cancelled: " + cancelledCount +
                        ", Preserved: " + preservedCount + " for doctor ID: " + doctorId);
            }
        } catch (Exception e) {
            System.err.println("Error handling appointments for doctor " + doctorId + ": " + e.getMessage());
            throw new RuntimeException("Failed to handle doctor appointments: " + e.getMessage());
        }
    }

    @Transactional
    public HospitalStaff reactivateDoctor(String id) {
        HospitalStaff doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        doctor.setIsActive(true);

        System.out.println("Doctor " + doctor.getName() + " reactivated. Previous end date preserved: " + doctor.getEndDate());

        return doctorRepository.save(doctor);
    }
}