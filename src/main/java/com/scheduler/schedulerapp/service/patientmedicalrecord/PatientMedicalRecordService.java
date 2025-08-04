package com.scheduler.schedulerapp.service.patientmedicalrecord;

import com.scheduler.schedulerapp.model.recordtab.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PatientMedicalRecordService {

    Optional<PatientMedicalRecord> getPatientMedicalRecord(String patientId);
    List<VisitHistory> getPatientVisitHistory(String patientId);
    Optional<HealthAnalytics> getPatientAnalytics(String patientId);
    Optional<PersonalMedicalInfo> getPersonalMedicalInfo(String patientId);

    PersonalMedicalInfo updatePersonalInfo(String patientId, PersonalMedicalInfo personalMedicalInfo);
    MedicalDocument uploadMedicalDocument(String patientId, MedicalDocument medicalDocument,
                                          String base64Data);

}
