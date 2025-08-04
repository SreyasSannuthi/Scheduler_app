package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.recordtab.PatientMedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PatientMedicalRecordRepository extends MongoRepository<PatientMedicalRecord, String> {

    Optional<PatientMedicalRecord> findByPatientId(String patientId);
}
