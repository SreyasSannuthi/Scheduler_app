package com.scheduler.schedulerapp.service.branchmapping;

import com.scheduler.schedulerapp.dto.DoctorBranchMappingInputDTO;
import com.scheduler.schedulerapp.model.StaffBranchMapping;

import java.util.List;

public interface DoctorBranchMappingService {
    StaffBranchMapping assignDoctorToBranch(DoctorBranchMappingInputDTO input);

    List<StaffBranchMapping> getDoctorBranches(String doctorId);

    List<StaffBranchMapping> getBranchDoctors(String branchId);

    void removeDoctorFromBranch(String doctorId, String branchId);

    List<StaffBranchMapping> getAllMappings();

    boolean isDoctorAssignedToBranch(String doctorId, String branchId);
}
