package com.scheduler.schedulerapp.service.doctor;

import com.scheduler.schedulerapp.model.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Optional<Doctor> getDoctorById(String id);
    List<Doctor> getDoctorsByRole(String role);
    Doctor createDoctor(Doctor doctor);
    Doctor updateDoctor(String id, Doctor doctor);
    void deleteDoctor(String id);
}
