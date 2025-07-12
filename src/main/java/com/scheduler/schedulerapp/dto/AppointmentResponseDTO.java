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
    private String userId;
    private String userName;
    private String startTime;
    private String endTime;
    private String status;
    private String category;
    private String categoryColor;
    private String duration;
}