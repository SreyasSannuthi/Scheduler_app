package com.scheduler.schedulerapp.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @Pattern(regexp = "scheduled|cancelled|completed", message = "Invalid status")
    private String status = "scheduled";

    @Pattern(regexp = "work|personal|medical|education|social", message = "Invalid category")
    private String category = "work";

    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        return endTime == null || startTime == null || endTime.isAfter(startTime);
    }

    @AssertTrue(message = "Appointment duration cannot exceed 4 hours")
    public boolean isValidDuration() {
        if (startTime == null || endTime == null) return true;
        return Duration.between(startTime, endTime).toHours() <= 4;
    }
}