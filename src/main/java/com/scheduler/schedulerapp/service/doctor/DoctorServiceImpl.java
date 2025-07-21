package com.scheduler.schedulerapp.service.doctor;

import com.scheduler.schedulerapp.model.Doctor;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
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
    public void deleteDoctor(String id) {doctorRepository.deleteById(id);
    }
}