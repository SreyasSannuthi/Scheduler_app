package com.scheduler.schedulerapp.dto.recordtabdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientMedicalRecordResponseDTO {
    private String id;
    private String patientId;
    private String patientName;
    private String createdAt;
    private String updatedAt;

    private List<VisitHistoryResponseDTO> visitHistory;
    private PersonalMedicalInfoResponseDTO personalInfo;
    private List<MedicalDocumentResponseDTO> medicalDocuments;
    private HealthAnalyticsResponseDTO analytics;
}