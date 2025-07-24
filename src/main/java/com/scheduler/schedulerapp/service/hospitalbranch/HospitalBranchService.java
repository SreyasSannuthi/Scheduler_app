package com.scheduler.schedulerapp.service.hospitalbranch;

import com.scheduler.schedulerapp.dto.HospitalBranchInputDTO;
import com.scheduler.schedulerapp.dto.HospitalBranchUpdateInputDTO;
import com.scheduler.schedulerapp.model.HospitalBranch;

import java.util.List;
import java.util.Optional;

public interface HospitalBranchService {
    HospitalBranch createBranch(HospitalBranchInputDTO input);
    List<HospitalBranch> getAllBranches();
    List<HospitalBranch> getActiveBranches();
    Optional<HospitalBranch> getBranchById(String id);
    HospitalBranch updateBranch(String id, HospitalBranchUpdateInputDTO input);
    void deleteBranch(String id);
}