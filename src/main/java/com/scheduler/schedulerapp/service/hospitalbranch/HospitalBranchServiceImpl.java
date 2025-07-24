package com.scheduler.schedulerapp.service.hospitalbranch;

import com.scheduler.schedulerapp.dto.HospitalBranchInputDTO;
import com.scheduler.schedulerapp.dto.HospitalBranchUpdateInputDTO;
import com.scheduler.schedulerapp.model.HospitalBranch;
import com.scheduler.schedulerapp.repository.HospitalBranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HospitalBranchServiceImpl implements HospitalBranchService {

    @Autowired
    private HospitalBranchRepository hospitalBranchRepository;

    @Override
    public HospitalBranch createBranch(HospitalBranchInputDTO input) {
        // Check if branch code already exists
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
            // Check if new branch code conflicts with existing ones
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
        if (input.getIsActive() != null) {
            existingBranch.setIsActive(input.getIsActive());
        }

        return hospitalBranchRepository.save(existingBranch);
    }

    @Override
    public void deleteBranch(String id) {
        if (!hospitalBranchRepository.existsById(id)) {
            throw new RuntimeException("Branch not found with ID: " + id);
        }
        hospitalBranchRepository.deleteById(id);
    }
}