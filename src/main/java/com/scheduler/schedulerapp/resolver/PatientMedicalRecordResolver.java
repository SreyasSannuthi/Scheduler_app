package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.recordtabdto.*;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.recordtab.MedicalDocument;
import com.scheduler.schedulerapp.model.recordtab.PersonalMedicalInfo;
import com.scheduler.schedulerapp.service.patientmedicalrecord.PatientMedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PatientMedicalRecordResolver {

    @Autowired
    private PatientMedicalRecordService patientMedicalRecordService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public PatientMedicalRecordResponseDTO getPatientMedicalRecord(@Argument String patientId) {
        return patientMedicalRecordService
                .getPatientMedicalRecord(patientId)
                .map(dtoMapper::toPatientMedicalRecordResponseDTO)
                .orElse(null);
    }

    @QueryMapping
    public List<VisitHistoryResponseDTO> getPatientHistory(@Argument String patientId) {
        return patientMedicalRecordService
                .getPatientVisitHistory(patientId)
                .stream()
                .map(dtoMapper::toVisitHistoryResponseDTO)
                .toList();
    }

    @QueryMapping
    public HealthAnalyticsResponseDTO getPatientAnalytics(@Argument String patientId) {
        return patientMedicalRecordService
                .getPatientAnalytics(patientId)
                .map(dtoMapper::toHealthAnalyticsResponseDTO)
                .orElse(null);
    }

    @QueryMapping
    public PersonalMedicalInfoResponseDTO getPatientPersonalInfo(@Argument String patientId) {
        return patientMedicalRecordService
                .getPersonalMedicalInfo(patientId)
                .map(dtoMapper::toPersonalMedicalInfoResponseDTO)
                .orElse(null);
    }

    @MutationMapping
    public PersonalMedicalInfoResponseDTO updatePersonalInfo(@Argument String patientId,
                                                              @Argument("input") PersonalMedicalInfoInputDTO personalMedicalInfo) {
        PersonalMedicalInfo updatedInfo =patientMedicalRecordService
                                    .updatePersonalInfo(patientId, dtoMapper.toPersonalMedicalInfo(personalMedicalInfo));

        return dtoMapper.toPersonalMedicalInfoResponseDTO(updatedInfo);
    }

    @MutationMapping
    public MedicalDocumentResponseDTO uploadMedicalDocument(@Argument String patientId,
                                                            @Valid @Argument("input") MedicalDocumentInputDTO medicalDocument) {
        try {

            MedicalDocument document = new MedicalDocument();
            document.setFileName(medicalDocument.getFileName());
            document.setDescription(medicalDocument.getDescription());

            MedicalDocument savedDocument =patientMedicalRecordService.uploadMedicalDocument(patientId, document, medicalDocument.getFileContentBase64());
            return dtoMapper.toMedicalDocumentResponseDTO(savedDocument);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload medical document: " + e.getMessage());
        }
    }
}
