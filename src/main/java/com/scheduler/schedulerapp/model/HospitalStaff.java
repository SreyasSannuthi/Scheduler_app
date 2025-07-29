package com.scheduler.schedulerapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import static com.scheduler.schedulerapp.model.UserRole.*;

import java.util.Collection;
import java.util.List;

@Document(collection = "hospitalStaff")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalStaff implements UserDetails {

    @Id
    private String id;

    @NotBlank(message = "Doctor name cannot be blank")
    private String name;

    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Role cannot be blank")
    private String role;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    private String startDate;

    private String endDate;

    private Boolean isActive = true;

    public UserRole getRoleEnum() {
        try {
            return UserRole.fromString(this.role);
        } catch (Exception e) {
            return DOCTOR;
        }
    }

    public void setRoleEnum(UserRole roleEnum) {
        this.role = roleEnum.getValue();
    }

    public boolean isAdmin() {
        return ADMIN.getValue().equalsIgnoreCase(this.role);
    }

    public boolean isDoctor() {
        return DOCTOR.getValue().equalsIgnoreCase(this.role);
    }

    public boolean isReceptionist() {
        return RECEPTIONIST.getValue().equalsIgnoreCase(this.role);
    }

    public boolean isCustomerCare() {
        return CUSTOMER_CARE.getValue().equalsIgnoreCase(this.role);
    }

    public boolean hasFullAppointmentAccess() {
        return isAdmin() || isCustomerCare();
    }

    public boolean hasBranchAppointmentAccess() {
        return isAdmin() || isCustomerCare() || isReceptionist();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}