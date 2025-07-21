package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.PatientResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.service.patient.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PatientResolver {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public List<PatientResponseDTO> patients() {
        return patientService.getAllPatients().stream()
                .map(dtoMapper::toPatientResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public PatientResponseDTO patient(@Argument String id) {
        return patientService.getPatientById(id)
                .map(dtoMapper::toPatientResponseDTO)
                .orElse(null);
    }

    @QueryMapping
    public List<PatientResponseDTO> patientsByRole(@Argument String role) {
        return patientService.getPatientsByRole(role).stream()
                .map(dtoMapper::toPatientResponseDTO)
                .collect(Collectors.toList());
    }
}
