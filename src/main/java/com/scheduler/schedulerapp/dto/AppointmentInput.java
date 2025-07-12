package com.scheduler.schedulerapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AppointmentInput {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Start time is required")
    private String startTime;

    @NotBlank(message = "End time is required")
    private String endTime;

    @Pattern(regexp = "scheduled|cancelled|completed", message = "Invalid status")
    private String status;

    @Pattern(regexp = "work|personal|medical|education|social", message = "Invalid category")
    private String category;
}
