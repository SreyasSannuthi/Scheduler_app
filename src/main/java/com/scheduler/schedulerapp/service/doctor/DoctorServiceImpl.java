package com.scheduler.schedulerapp.service.doctor;

import com.scheduler.schedulerapp.model.Doctor;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllWhoIsActive();
    }

    @Override
    public Optional<Doctor> getDoctorById(String id) {
        return doctorRepository.findById(id);
    }

    @Override
    public List<Doctor> getDoctorsByRole(String role) {
        return doctorRepository.findByRole(role);
    }

    @Override
    public Doctor createDoctor(Doctor doctor) {

        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }

        doctor.setStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
        doctor.setEndDate("");
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(String id, Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }
        doctor.setId(id);
        return doctorRepository.save(doctor);
    }

    @Override
    public void deleteDoctor(String id) {
        Optional<Doctor> doctorCheck = doctorRepository.findById(id);
        if (doctorCheck.isPresent()) {
            Doctor doctor = doctorCheck.get();
            doctor.setIsActive(false);
            doctor.setEndDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
            doctorRepository.save(doctor);
        }

        //doctorRepository.deleteById(id);
    }
}