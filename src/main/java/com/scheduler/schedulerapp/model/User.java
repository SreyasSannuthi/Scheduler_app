package com.scheduler.schedulerapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {

    @Id
    private String id;

    @NotBlank(message = "Username cannot be blank")
    private String name;

    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "admin|user", message = "Role must be either 'admin' or 'user'")
    private String role;

    private boolean isActive =true;
}
