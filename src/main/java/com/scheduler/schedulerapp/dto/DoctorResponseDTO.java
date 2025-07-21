package com.scheduler.schedulerapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDTO {
    private String id;
    private String name;
    private String email;
    private String role;
}