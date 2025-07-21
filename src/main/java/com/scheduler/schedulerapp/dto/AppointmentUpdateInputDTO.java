package com.scheduler.schedulerapp.dto;

import lombok.Data;

@Data
public class AppointmentUpdateInputDTO {
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String status;
}