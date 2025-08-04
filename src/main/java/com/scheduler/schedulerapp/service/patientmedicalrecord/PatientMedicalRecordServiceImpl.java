package com.scheduler.schedulerapp.service.patientmedicalrecord;

import com.scheduler.schedulerapp.model.recordtab.*;
import com.scheduler.schedulerapp.repository.PatientMedicalRecordRepository;
import com.scheduler.schedulerapp.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import com.scheduler.schedulerapp.repository.PatientRepository;

@Service
public class PatientMedicalRecordServiceImpl implements PatientMedicalRecordService {

    @Autowired
    private PatientMedicalRecordRepository patientMedicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AuthService authService;

    @Override
    public Optional<PatientMedicalRecord> getPatientMedicalRecord(String patientId) {
        return patientMedicalRecordRepository.findByPatientId(patientId);
    }

    @Override
    public List<VisitHistory> getPatientVisitHistory(String patientId) {
        Optional<PatientMedicalRecord> record = getPatientMedicalRecord(patientId);
        return record.map(PatientMedicalRecord::getVisitHistory)
                .orElse(new ArrayList<>());
    }

    @Override
    public Optional<HealthAnalytics> getPatientAnalytics(String patientId) {
        Optional<PatientMedicalRecord> record = getPatientMedicalRecord(patientId);
        return record.map(PatientMedicalRecord::getAnalytics);
    }

    @Override
    public Optional<PersonalMedicalInfo> getPersonalMedicalInfo(String patientId) {
        Optional<PatientMedicalRecord> record = getPatientMedicalRecord(patientId);
        return record.map(PatientMedicalRecord::getPersonalInfo);
    }

    @Override
    public PersonalMedicalInfo updatePersonalInfo(String patientId, PersonalMedicalInfo personalMedicalInfo) {
        if (personalMedicalInfo == null) {
            throw new IllegalArgumentException("PersonalMedicalInfo cannot be null");
        }

        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be null or empty");
        }

        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        }

        Optional<PatientMedicalRecord> record = getPatientMedicalRecord(patientId);

        if (record.isEmpty()) {
            PatientMedicalRecord newRecord = new PatientMedicalRecord();
            newRecord.setPatientId(patientId);
            newRecord.setPersonalInfo(personalMedicalInfo);

            newRecord.setCreatedAt(LocalDateTime.now());
            newRecord.setUpdatedAt(LocalDateTime.now());

            newRecord.setVisitHistory(new ArrayList<>());
            newRecord.setMedicalDocuments(new ArrayList<>());
            newRecord.setAnalytics(new HealthAnalytics());

            patientMedicalRecordRepository.save(newRecord);
            return personalMedicalInfo;
        }
        else {
            PatientMedicalRecord existingRecord = record.get();
            existingRecord.setPersonalInfo(personalMedicalInfo);

            existingRecord.setUpdatedAt(LocalDateTime.now());

            patientMedicalRecordRepository.save(existingRecord);
            return personalMedicalInfo;
        }
    }

    @Override
    public MedicalDocument uploadMedicalDocument(String patientId, MedicalDocument document, String base64Data) {
        // Validation
        if (document == null) {
            throw new IllegalArgumentException("MedicalDocument cannot be null");
        }

        if (base64Data == null || base64Data.trim().isEmpty()) {
            throw new IllegalArgumentException("File data cannot be null or empty");
        }

        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        }

        try {
            // 1. Save file to local storage
            String filePath = saveFileToLocalStorage(document.getFileName(), base64Data);

            // 2. Complete the MedicalDocument object
            document.setDocumentId(UUID.randomUUID().toString());
            document.setFilePath(filePath);
            document.setFileSize(calculateFileSize(base64Data));
            document.setUploadDate(LocalDateTime.now());
            document.setUploadedBy(authService.getCurrentUserId());

            // 3. Add to patient record (your existing logic)
            Optional<PatientMedicalRecord> recordOpt = getPatientMedicalRecord(patientId);

            if (recordOpt.isEmpty()) {
                // Create new record
                PatientMedicalRecord newRecord = new PatientMedicalRecord();
                newRecord.setPatientId(patientId);
                newRecord.setCreatedAt(LocalDateTime.now());
                newRecord.setUpdatedAt(LocalDateTime.now());
                newRecord.setVisitHistory(new ArrayList<>());
                newRecord.setMedicalDocuments(new ArrayList<>());
                newRecord.setPersonalInfo(new PersonalMedicalInfo());
                newRecord.setAnalytics(new HealthAnalytics());

                newRecord.getMedicalDocuments().add(document);
                patientMedicalRecordRepository.save(newRecord);
                return document;
            }
            else {
                // Update existing record
                PatientMedicalRecord record = recordOpt.get();
                record.getMedicalDocuments().add(document);
                record.setUpdatedAt(LocalDateTime.now());
                patientMedicalRecordRepository.save(record);
                return document;
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    // Helper methods
    private String saveFileToLocalStorage(String fileName, String base64Data) throws IOException {
        String uploadDir = "uploads/medical-documents/";
        String fileExtension = getFileExtension(fileName);
        String uniqueFileName = UUID.randomUUID().toString() + "_" +
                System.currentTimeMillis() + fileExtension;
        String filePath = uploadDir + uniqueFileName;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        Files.write(Paths.get(filePath), fileBytes);
        return filePath;
    }

    private Long calculateFileSize(String base64Data) {
        // Base64 adds ~33% overhead, so actual file size is:
        return (long) (base64Data.length() * 0.75);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
}