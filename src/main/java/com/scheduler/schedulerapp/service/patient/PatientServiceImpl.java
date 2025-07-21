package com.scheduler.schedulerapp.service.patient;

import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientById(String id) {
        return patientRepository.findById(id);
    }

    @Override
    public List<Patient> getPatientsByRole(String role) {
        return patientRepository.findByRole(role);
    }

    @Override
    public Patient createPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        return patientRepository.save(patient);
    }

    @Override
    public Patient updatePatient(String id, Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        patient.setId(id);
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(String id) {patientRepository.deleteById(id);
    }
}