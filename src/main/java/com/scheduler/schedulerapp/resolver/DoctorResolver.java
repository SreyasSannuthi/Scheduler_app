package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.DoctorResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.service.doctor.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DoctorResolver {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public List<DoctorResponseDTO> doctors() {
        return doctorService.getAllDoctors().stream()
                .map(dtoMapper::toDoctorResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public DoctorResponseDTO doctor(@Argument String id) {
        return doctorService.getDoctorById(id)
                .map(dtoMapper::toDoctorResponseDTO)
                .orElse(null);
    }

    @QueryMapping
    public List<DoctorResponseDTO> doctorsByRole(@Argument String role) {
        return doctorService.getDoctorsByRole(role).stream()
                .map(dtoMapper::toDoctorResponseDTO)
                .collect(Collectors.toList());
    }

    @MutationMapping
    public Boolean deleteDoctor(@Argument String id) {
        try {
            doctorService.deleteDoctor(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete doctor: " + e.getMessage());
        }
    }
}
