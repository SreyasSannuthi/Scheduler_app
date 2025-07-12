package com.scheduler.schedulerapp.dto;

import lombok.Data;

@Data
public class AppointmentUpdateInput {
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String status;
    private String category;
}