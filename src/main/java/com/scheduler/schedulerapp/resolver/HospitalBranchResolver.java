package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.HospitalBranchInputDTO;
import com.scheduler.schedulerapp.dto.HospitalBranchResponseDTO;
import com.scheduler.schedulerapp.dto.HospitalBranchUpdateInputDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.HospitalBranch;
import com.scheduler.schedulerapp.service.hospitalbranch.HospitalBranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HospitalBranchResolver {

    @Autowired
    private HospitalBranchService hospitalBranchService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public List<HospitalBranchResponseDTO> hospitalBranches() {
        return hospitalBranchService.getAllBranches().stream()
                .map(dtoMapper::toHospitalBranchResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<HospitalBranchResponseDTO> activeBranches() {
        return hospitalBranchService.getActiveBranches().stream()
                .map(dtoMapper::toHospitalBranchResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public HospitalBranchResponseDTO hospitalBranch(@Argument String id) {
        return hospitalBranchService.getBranchById(id)
                .map(dtoMapper::toHospitalBranchResponseDTO)
                .orElse(null);
    }

    @MutationMapping
    public HospitalBranchResponseDTO createHospitalBranch(@Valid @Argument("input") HospitalBranchInputDTO input) {
        try {
            HospitalBranch createdBranch = hospitalBranchService.createBranch(input);
            return dtoMapper.toHospitalBranchResponseDTO(createdBranch);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create hospital branch: " + e.getMessage());
        }
    }

    @MutationMapping
    public HospitalBranchResponseDTO updateHospitalBranch(@Argument String id,
                                                          @Valid @Argument("input") HospitalBranchUpdateInputDTO input) {
        try {
            HospitalBranch updatedBranch = hospitalBranchService.updateBranch(id, input);
            return dtoMapper.toHospitalBranchResponseDTO(updatedBranch);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update hospital branch: " + e.getMessage());
        }
    }

    @MutationMapping
    public Boolean deleteHospitalBranch(@Argument String id) {
        try {
            hospitalBranchService.deleteBranch(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete hospital branch: " + e.getMessage());
        }
    }
}