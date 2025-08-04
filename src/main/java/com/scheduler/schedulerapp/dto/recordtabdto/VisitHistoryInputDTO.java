package com.scheduler.schedulerapp.dto.recordtabdto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VisitHistoryInputDTO {

    @NotBlank(message = "Appointment ID is required")
    private String appointmentId;

    @Size(max = 200, message = "Reason for visit too long")
    private String reasonForVisit;

    @Size(max = 500, message = "Symptoms description too long")
    private String symptoms;

    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "Blood pressure format: 120/80")
    private String bloodPressure;

    @Min(value = 90, message = "Temperature too low")
    @Max(value = 115, message = "Temperature too high")
    private Double temperature;

    @Min(value = 1, message = "Weight must be positive")
    @Max(value = 500, message = "Weight too high")
    private Double weight;

    @Size(max = 300, message = "Diagnosis too long")
    private String diagnosis;

    @Size(max = 500, message = "Treatment description too long")
    private String treatment;

    @Size(max = 500, message = "Medications list too long")
    private String medications;

    @Size(max = 1000, message = "Doctor notes too long")
    private String doctorNotes;
}
