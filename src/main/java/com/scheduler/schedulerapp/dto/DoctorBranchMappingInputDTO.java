package com.scheduler.schedulerapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoctorBranchMappingInputDTO {

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotBlank(message = "Branch ID is required")
    private String branchId;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotBlank(message = "Branch code is required")
    private String branchCode;
}