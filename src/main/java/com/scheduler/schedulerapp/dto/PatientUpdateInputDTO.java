package com.scheduler.schedulerapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientUpdateInputDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private Integer age;
    private Boolean isActive;
}