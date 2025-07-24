package com.scheduler.schedulerapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HospitalBranchInputDTO {

    @NotBlank(message = "Branch code is required")
    private String branchCode;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    private String zipCode;

    @Email(message = "Please provide valid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
