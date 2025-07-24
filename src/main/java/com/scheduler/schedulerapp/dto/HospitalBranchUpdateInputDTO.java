package com.scheduler.schedulerapp.dto;

import lombok.Data;

@Data
public class HospitalBranchUpdateInputDTO {
    private String branchCode;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
}