package com.scheduler.schedulerapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalBranchResponseDTO {
    private String id;
    private String branchCode;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private String startedAt; // Formatted date string
}