package com.scheduler.schedulerapp.model.recordtab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalMedicalInfo {
    private String bloodType;
    private Double height; // cm
    private Double weight; // kg
    private LocalDateTime lastWeightUpdate;

    private List<String> allergies = new ArrayList<>();
    private List<String> chronicConditions = new ArrayList<>();
    private List<String> currentMedications = new ArrayList<>();

    private String emergencyContactName;
    private String emergencyContactPhone;
}