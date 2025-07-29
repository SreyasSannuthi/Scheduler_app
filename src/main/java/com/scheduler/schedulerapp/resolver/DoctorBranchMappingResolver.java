package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.DoctorBranchMappingInputDTO;
import com.scheduler.schedulerapp.dto.DoctorBranchMappingResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.StaffBranchMapping;
import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.HospitalBranch;
import com.scheduler.schedulerapp.service.branchmapping.DoctorBranchMappingService;
import com.scheduler.schedulerapp.service.hospitalbranch.HospitalBranchService;
import com.scheduler.schedulerapp.service.doctor.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DoctorBranchMappingResolver {

    @Autowired
    private DoctorBranchMappingService mappingService;

    @Autowired
    private HospitalBranchService branchService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public List<DoctorBranchMappingResponseDTO> doctorBranchMappings() {
        return mappingService.getAllMappings().stream()
                .map(this::enrichMappingWithNames)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<DoctorBranchMappingResponseDTO> doctorBranches(@Argument String doctorId) {
        return mappingService.getDoctorBranches(doctorId).stream()
                .map(this::enrichMappingWithNames)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<DoctorBranchMappingResponseDTO> branchDoctors(@Argument String branchId) {
        return mappingService.getBranchDoctors(branchId).stream()
                .map(this::enrichMappingWithNames)
                .collect(Collectors.toList());
    }

    @MutationMapping
    public DoctorBranchMappingResponseDTO assignDoctorToBranch(
            @Valid @Argument("input") DoctorBranchMappingInputDTO input) {
        try {
            StaffBranchMapping mapping = mappingService.assignDoctorToBranch(input);
            return enrichMappingWithNames(mapping);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign doctor to branch: " + e.getMessage());
        }
    }

    @MutationMapping
    public Boolean removeDoctorFromBranch(@Argument String doctorId, @Argument String branchId) {
        try {
            mappingService.removeDoctorFromBranch(doctorId, branchId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove doctor from branch: " + e.getMessage());
        }
    }

    private DoctorBranchMappingResponseDTO enrichMappingWithNames(StaffBranchMapping mapping) {
        String doctorName = doctorService.getDoctorById(mapping.getDoctorId())
                .map(HospitalStaff::getName)
                .orElse("Unknown Doctor");

        String branchCode = branchService.getBranchById(mapping.getBranchId())
                .map(HospitalBranch::getBranchCode)
                .orElse("Unknown Branch");

        return dtoMapper.toDoctorBranchMappingResponseDTO(mapping, doctorName, branchCode);
    }
}