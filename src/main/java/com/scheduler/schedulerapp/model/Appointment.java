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
    private String title;
    private String description;
    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status = "scheduled";
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