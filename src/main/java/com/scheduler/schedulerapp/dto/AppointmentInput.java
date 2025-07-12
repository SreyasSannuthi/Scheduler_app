package com.scheduler.schedulerapp.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

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

    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) return true;
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            return end.isAfter(start);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @AssertTrue(message = "Appointment duration cannot exceed 4 hours")
    public boolean isValidDuration() {
        if (startTime == null || endTime == null) return true;
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            return Duration.between(start, end).toHours() <= 4;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
