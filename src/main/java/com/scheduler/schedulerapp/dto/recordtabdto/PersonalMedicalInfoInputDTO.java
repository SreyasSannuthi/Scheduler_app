package com.scheduler.schedulerapp.dto.recordtabdto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class PersonalMedicalInfoInputDTO {

    @Pattern(regexp = "^(A\\+|A-|B\\+|B-|AB\\+|AB-|O\\+|O-)$", message = "Invalid blood type")
    private String bloodType;

    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height cannot exceed 300 cm")
    private Double height;

    @Min(value = 1, message = "Weight must be at least 1 kg")
    @Max(value = 500, message = "Weight cannot exceed 500 kg")
    private Double weight;

    @Size(max = 10, message = "Cannot have more than 10 allergies")
    private List<String> allergies;

    @Size(max = 15, message = "Cannot have more than 15 chronic conditions")
    private List<String> chronicConditions;

    @Size(max = 20, message = "Cannot have more than 20 current medications")
    private List<String> currentMedications;

    @NotBlank(message = "Emergency contact name is required")
    @Size(max = 100, message = "Emergency contact name too long")
    private String emergencyContactName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String emergencyContactPhone;
}
