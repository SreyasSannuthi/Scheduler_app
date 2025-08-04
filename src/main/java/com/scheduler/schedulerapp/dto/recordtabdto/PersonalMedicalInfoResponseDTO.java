package com.scheduler.schedulerapp.dto.recordtabdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalMedicalInfoResponseDTO {
    private String bloodType;
    private Double height;
    private Double weight;
    private String lastWeightUpdate;
    private List<String> allergies;
    private List<String> chronicConditions;
    private List<String> currentMedications;
    private String emergencyContactName;
    private String emergencyContactPhone;
}