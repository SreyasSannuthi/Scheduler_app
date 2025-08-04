package com.scheduler.schedulerapp.model.recordtab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "patientMedicalRecords")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientMedicalRecord {

    @Id
    private String id;
    private String patientId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<VisitHistory> visitHistory = new ArrayList<>();

    private PersonalMedicalInfo personalInfo;
    private List<MedicalDocument> medicalDocuments = new ArrayList<>();

    private HealthAnalytics analytics;
}