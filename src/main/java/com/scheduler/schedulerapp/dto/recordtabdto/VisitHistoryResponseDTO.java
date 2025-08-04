package com.scheduler.schedulerapp.dto.recordtabdto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitHistoryResponseDTO {
    private String appointmentId;
    private String visitDate;
    private String doctorName;
    private String branchLocation;
    private String reasonForVisit;
    private String symptoms;
    private String bloodPressure;
    private Double temperature;
    private Double weight;
    private String diagnosis;
    private String treatment;
    private String medications;
    private String doctorNotes;
    private Boolean followUpRequired;
    private String followUpDate;
    private String followUpInstructions;
    private String visitDuration;
}