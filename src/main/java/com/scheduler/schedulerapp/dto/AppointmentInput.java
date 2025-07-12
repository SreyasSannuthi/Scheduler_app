package com.scheduler.schedulerapp.dto;

import lombok.Data;

@Data
public class AppointmentInput {
    private String title;
    private String description;
    private String userId;
    private String startTime;
    private String endTime;
    private String status;
    private String category;
}