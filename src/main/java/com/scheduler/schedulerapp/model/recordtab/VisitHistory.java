package com.scheduler.schedulerapp.model.recordtab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitHistory {
    private String appointmentId;
    private String consultationSessionId;
    private LocalDateTime visitDate;
    private String doctorId;
    private String doctorName;
    private String branchId;
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
    private LocalDateTime followUpDate;
    private String followUpInstructions;
    private String visitDuration;
}
