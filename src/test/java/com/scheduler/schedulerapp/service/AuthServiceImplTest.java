package com.scheduler.schedulerapp.service;

import com.scheduler.schedulerapp.dto.AuthRequestDTO;
import com.scheduler.schedulerapp.dto.AuthResponseDTO;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import com.scheduler.schedulerapp.security.JwtUtil;
import com.scheduler.schedulerapp.service.auth.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.Patient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String PASSWORD = "secret";

    private HospitalStaff doctorUser(String email) {
        return new HospitalStaff("1", "Dr. John Doe", email, "doctor", "encPwd", "July 22 2025 5:51 PM", "", true);
    }

    private Patient patientUser(String email) {
        return new Patient("1", "Jane Smith", email, "1234567890", 30, "patient", "encPwd", true);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("authenticate(): success for Doctor")
    void shouldAuthenticateDoctorSuccessfully() {
        String email = "doc@example.com";
        HospitalStaff doctor = doctorUser(email);
        AuthRequestDTO request = new AuthRequestDTO(email, PASSWORD);

        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
        when(jwtUtil.generateToken(doctor)).thenReturn("jwt-token-doctor");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        AuthResponseDTO response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("jwt-token-doctor", response.getToken());
        assertEquals(email, response.getUsername());
        assertEquals("DOCTOR", response.getRole());
        assertEquals("Authentication successful", response.getMessage());

        verify(jwtUtil).generateToken(doctor);
    }

    @Test
    @DisplayName("authenticate(): success for Patient")
    void shouldAuthenticatePatientSuccessfully() {
        String email = "pat@example.com";
        Patient patient = patientUser(email);
        AuthRequestDTO request = new AuthRequestDTO(email, PASSWORD);

        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
        when(jwtUtil.generateToken(patient)).thenReturn("jwt-token-patient");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        AuthResponseDTO response = authService.authenticate(request);

        assertEquals("jwt-token-patient", response.getToken());
        assertEquals(email, response.getUsername());
        assertEquals("PATIENT", response.getRole());
        assertEquals("Authentication successful", response.getMessage());
    }

    @Test
    @DisplayName("authenticate(): user not found")
    void shouldReturnUserNotFound() {
        String email = "ghost@example.com";
        AuthRequestDTO request = new AuthRequestDTO(email, PASSWORD);

        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        AuthResponseDTO response = authService.authenticate(request);

        assertNull(response.getToken());
        assertNull(response.getUsername());
        assertNull(response.getRole());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    @DisplayName("authenticate(): authentication failure (BadCredentials)")
    void shouldReturnFailureMessageWhenAuthenticationFails() {
        String email = "wrong@example.com";
        AuthRequestDTO request = new AuthRequestDTO(email, PASSWORD);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthResponseDTO response = authService.authenticate(request);

        assertNull(response.getToken());
        assertNull(response.getUsername());
        assertNull(response.getRole());
        assertTrue(response.getMessage().startsWith("Authentication failed"));

        verifyNoInteractions(doctorRepository, patientRepository, jwtUtil);
    }
}
