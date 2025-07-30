package com.scheduler.schedulerapp.service.auth;

import com.scheduler.schedulerapp.dto.AuthRequestDTO;
import com.scheduler.schedulerapp.dto.AuthResponseDTO;
import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import com.scheduler.schedulerapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails userDetails = findUserByEmail(request.getUsername());
            if (userDetails == null) {
                return new AuthResponseDTO(null, null, null, "User not found");
            }

            String token = jwtUtil.generateToken(userDetails);
            String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

            return new AuthResponseDTO(token, userDetails.getUsername(), role, "Authentication successful");
        } catch (Exception e) {
            return new AuthResponseDTO(null, null, null, "Authentication failed: " + e.getMessage());
        }
    }

    private UserDetails findUserByEmail(String email) {
        var doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) {
            return doctor.get();
        }

        var patient = patientRepository.findByEmail(email);
        return patient.orElse(null);
    }

    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String email = auth.getName();

            Optional<HospitalStaff> doctor = doctorRepository.findByEmail(email);
            if (doctor.isPresent()) {
                return doctor.get().getId();
            }

            Optional<Patient> patient = patientRepository.findByEmail(email);
            if (patient.isPresent()) {
                return patient.get().getId();
            }
        }
        return "SYSTEM";
    }

    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String email = auth.getName();

            Optional<HospitalStaff> doctor = doctorRepository.findByEmail(email);
            if (doctor.isPresent()) {
                return doctor.get().getName();
            }

            Optional<Patient> patient = patientRepository.findByEmail(email);
            if (patient.isPresent()) {
                return patient.get().getName();
            }
        }
        return "System";
    }
}