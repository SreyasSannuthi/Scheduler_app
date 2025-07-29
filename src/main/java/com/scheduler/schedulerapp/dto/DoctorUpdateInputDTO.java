package com.scheduler.schedulerapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DoctorUpdateInputDTO {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Please enter a valid email address")
    private String email;

    private String role;

    private Boolean isActive;
}