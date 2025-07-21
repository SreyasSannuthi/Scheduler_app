package com.scheduler.schedulerapp.service.patient;

import com.scheduler.schedulerapp.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    List<Patient> getAllPatients();
    Optional<Patient> getPatientById(String id);
    List<Patient> getPatientsByRole(String role);
    Patient createPatient(Patient patient);
    Patient updatePatient(String id, Patient patient);
    void deletePatient(String id);
}
