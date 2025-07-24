package com.scheduler.schedulerapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    private String id;
    private String title;
    private String description;
    private String doctorId;
    private String patientId;
    private String doctorName;
    private String patientName;
    private String startTime;
    private String endTime;
    private String createdAt;
    private String updatedAt;
    private String status;
    private String duration;
    private String branchId;
    private String branchLocation;
}