package com.scheduler.schedulerapp.service.branchmapping;

import com.scheduler.schedulerapp.dto.DoctorBranchMappingInputDTO;
import com.scheduler.schedulerapp.model.DoctorBranchMapping;

import java.util.List;

public interface DoctorBranchMappingService {
    DoctorBranchMapping assignDoctorToBranch(DoctorBranchMappingInputDTO input);
    List<DoctorBranchMapping> getDoctorBranches(String doctorId);
    List<DoctorBranchMapping> getBranchDoctors(String branchId);
    void removeDoctorFromBranch(String doctorId, String branchId);
    List<DoctorBranchMapping> getAllMappings();
}
