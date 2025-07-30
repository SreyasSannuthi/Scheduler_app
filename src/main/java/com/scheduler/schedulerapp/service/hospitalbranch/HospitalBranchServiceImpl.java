package com.scheduler.schedulerapp.service.hospitalbranch;

import com.scheduler.schedulerapp.dto.HospitalBranchInputDTO;
import com.scheduler.schedulerapp.dto.HospitalBranchUpdateInputDTO;
import com.scheduler.schedulerapp.model.HospitalBranch;
import com.scheduler.schedulerapp.model.StaffBranchMapping;
import com.scheduler.schedulerapp.repository.HospitalBranchRepository;
import com.scheduler.schedulerapp.repository.DoctorBranchMappingRepository;
import com.scheduler.schedulerapp.service.activitylogservice.ActivityLogService;
import com.scheduler.schedulerapp.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HospitalBranchServiceImpl implements HospitalBranchService {

    @Autowired
    private HospitalBranchRepository hospitalBranchRepository;

    @Autowired
    private DoctorBranchMappingRepository doctorBranchMappingRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private AuthService authService;

    @Override
    public HospitalBranch createBranch(HospitalBranchInputDTO input) {
        if (hospitalBranchRepository.existsByBranchCode(input.getBranchCode())) {
            throw new RuntimeException("Branch code already exists: " + input.getBranchCode());
        }

        HospitalBranch branch = new HospitalBranch();
        branch.setBranchCode(input.getBranchCode());
        branch.setAddress(input.getAddress());
        branch.setCity(input.getCity());
        branch.setState(input.getState());
        branch.setZipCode(input.getZipCode());
        branch.setEmail(input.getEmail());
        branch.setPhoneNumber(input.getPhoneNumber());
        branch.setStartedAt(LocalDateTime.now());
        branch.setIsActive(true);

        return hospitalBranchRepository.save(branch);
    }

    @Override
    public List<HospitalBranch> getAllBranches() {
        return hospitalBranchRepository.findAll();
    }

    @Override
    public List<HospitalBranch> getActiveBranches() {
        return hospitalBranchRepository.findByIsActiveTrue();
    }

    @Override
    public Optional<HospitalBranch> getBranchById(String id) {
        return hospitalBranchRepository.findById(id);
    }

    @Override
    public HospitalBranch updateBranch(String id, HospitalBranchUpdateInputDTO input) {
        HospitalBranch existingBranch = hospitalBranchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + id));

        if (input.getBranchCode() != null) {
            if (!input.getBranchCode().equals(existingBranch.getBranchCode()) &&
                    hospitalBranchRepository.existsByBranchCode(input.getBranchCode())) {
                throw new RuntimeException("Branch code already exists: " + input.getBranchCode());
            }
            existingBranch.setBranchCode(input.getBranchCode());
        }
        if (input.getAddress() != null) {
            existingBranch.setAddress(input.getAddress());
        }
        if (input.getCity() != null) {
            existingBranch.setCity(input.getCity());
        }
        if (input.getState() != null) {
            existingBranch.setState(input.getState());
        }
        if (input.getZipCode() != null) {
            existingBranch.setZipCode(input.getZipCode());
        }
        if (input.getEmail() != null) {
            existingBranch.setEmail(input.getEmail());
        }
        if (input.getPhoneNumber() != null) {
            existingBranch.setPhoneNumber(input.getPhoneNumber());
        }

        if (input.getIsActive() != null && !input.getIsActive().equals(existingBranch.getIsActive())) {
            existingBranch.setIsActive(input.getIsActive());

            if (!input.getIsActive()) {
                existingBranch.setClosedAt(LocalDateTime.now().toString());

                String impactSummary = String.format("Branch closed with %d staff mappings removed",
                        doctorBranchMappingRepository.countByBranchId(id));

                activityLogService.logBranchDeactivation(
                        id,
                        existingBranch.getBranchCode(),
                        authService.getCurrentUserId(),
                        authService.getCurrentUserName(),
                        impactSummary
                );

                removeDoctorMappingsForBranch(id);
            } else {
                activityLogService.logBranchReactivation(
                        id,
                        existingBranch.getBranchCode(),
                        authService.getCurrentUserId(),
                        authService.getCurrentUserName()
                );
                existingBranch.setClosedAt("");
            }
        }

        return hospitalBranchRepository.save(existingBranch);
    }

    @Override
    @Transactional
    public void deleteBranch(String id) {
        if (!hospitalBranchRepository.existsById(id)) {
            throw new RuntimeException("Branch not found with ID: " + id);
        }

        Optional<HospitalBranch> branchCheck = hospitalBranchRepository.findById(id);
        if (branchCheck.isPresent()) {
            HospitalBranch branch = branchCheck.get();

            branch.setIsActive(false);
            branch.setClosedAt(LocalDateTime.now().toString());

            String impactSummary = String.format("Branch closed with %d staff mappings removed",
                    doctorBranchMappingRepository.countByBranchId(id));

            activityLogService.logBranchDeactivation(
                    id,
                    branch.getBranchCode(),
                    authService.getCurrentUserId(),
                    authService.getCurrentUserName(),
                    impactSummary
            );

            removeDoctorMappingsForBranch(id);
            hospitalBranchRepository.save(branch);

            System.out.println("Branch " + branch.getBranchCode() + " has been deactivated and all doctor mappings removed");
        }
    }

    private void removeDoctorMappingsForBranch(String branchId) {
        try {
            List<StaffBranchMapping> mappings = doctorBranchMappingRepository.findByBranchId(branchId);

            if (!mappings.isEmpty()) {
                doctorBranchMappingRepository.deleteAll(mappings);
                System.out.println("Removed " + mappings.size() + " doctor mappings for branch ID: " + branchId);
            }
        } catch (Exception e) {
            System.err.println("Error removing doctor mappings for branch " + branchId + ": " + e.getMessage());
            throw new RuntimeException("Failed to remove doctor mappings for branch: " + e.getMessage());
        }
    }
}