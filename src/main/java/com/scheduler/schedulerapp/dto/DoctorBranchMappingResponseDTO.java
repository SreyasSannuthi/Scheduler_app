package com.scheduler.schedulerapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorBranchMappingResponseDTO {
    private String id;
    private String doctorId;
    private String branchId;

    private String doctorName;
    private String branchCode;
}
