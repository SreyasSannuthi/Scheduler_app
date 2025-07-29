package com.scheduler.schedulerapp.service.branchmapping;

import com.scheduler.schedulerapp.dto.DoctorBranchMappingInputDTO;
import com.scheduler.schedulerapp.model.StaffBranchMapping;
import com.scheduler.schedulerapp.repository.DoctorBranchMappingRepository;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.HospitalBranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorBranchMappingServiceImpl implements DoctorBranchMappingService {

    @Autowired
    private DoctorBranchMappingRepository mappingRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HospitalBranchRepository hospitalBranchRepository;

    @Override
    public StaffBranchMapping assignDoctorToBranch(DoctorBranchMappingInputDTO input) {

        if (!doctorRepository.existsById(input.getDoctorId())) {
            throw new RuntimeException("Doctor not found with ID: " + input.getDoctorId());
        }

        if (!hospitalBranchRepository.existsById(input.getBranchId())) {
            throw new RuntimeException("Branch not found with ID: " + input.getBranchId());
        }

        Optional<StaffBranchMapping> existingMapping = mappingRepository
                .findByDoctorIdAndBranchId(input.getDoctorId(), input.getBranchId());

        if (existingMapping.isPresent()) {
            throw new RuntimeException("Doctor is already assigned to this branch");
        }

        StaffBranchMapping mapping = new StaffBranchMapping();
        mapping.setDoctorId(input.getDoctorId());
        mapping.setBranchId(input.getBranchId());
        mapping.setDoctorName(input.getDoctorName());
        mapping.setBranchCode(input.getBranchCode());

        return mappingRepository.save(mapping);
    }

    @Override
    public List<StaffBranchMapping> getDoctorBranches(String doctorId) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("Doctor not found with ID: " + doctorId);
        }

        return mappingRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<StaffBranchMapping> getBranchDoctors(String branchId) {

        if (!hospitalBranchRepository.existsById(branchId)) {
            throw new RuntimeException("Branch not found with ID: " + branchId);
        }

        return mappingRepository.findByBranchId(branchId);
    }

    @Override
    public void removeDoctorFromBranch(String doctorId, String branchId) {
        Optional<StaffBranchMapping> mapping = mappingRepository
                .findByDoctorIdAndBranchId(doctorId, branchId);

        if (mapping.isEmpty()) {
            throw new RuntimeException("Doctor is not assigned to this branch");
        }

        mappingRepository.deleteByDoctorIdAndBranchId(doctorId, branchId);
    }

    @Override
    public List<StaffBranchMapping> getAllMappings() {
        return mappingRepository.findAll();
    }

    @Override
    public boolean isDoctorAssignedToBranch(String doctorId, String branchId) {
        Optional<StaffBranchMapping> mapping = mappingRepository
                .findByDoctorIdAndBranchId(doctorId, branchId);
        return mapping.isPresent();
    }
}