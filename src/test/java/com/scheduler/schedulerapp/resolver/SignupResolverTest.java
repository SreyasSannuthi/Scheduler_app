package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.DoctorSignupInputDTO;
import com.scheduler.schedulerapp.dto.PatientSignupInputDTO;
import com.scheduler.schedulerapp.dto.SignupResponseDTO;
import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignupResolverTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SignupResolver signupResolver;

    private DoctorSignupInputDTO doctorSignupInput;
    private PatientSignupInputDTO patientSignupInput;
    private HospitalStaff mockDoctor;
    private Patient mockPatient;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_ENCODED_PASSWORD = "encoded_password_123";
    private static final String TEST_ID = "test-id-123";
    private static final String TEST_PHONE = "1234567890";
    private static final Integer TEST_AGE = 30;

    @BeforeEach
    void setUp() {
        // Setup doctor signup input
        doctorSignupInput = new DoctorSignupInputDTO();
        doctorSignupInput.setName(TEST_NAME);
        doctorSignupInput.setEmail(TEST_EMAIL);
        doctorSignupInput.setPassword(TEST_PASSWORD);
        doctorSignupInput.setConfirmPassword(TEST_PASSWORD);

        // Setup patient signup input
        patientSignupInput = new PatientSignupInputDTO();
        patientSignupInput.setName(TEST_NAME);
        patientSignupInput.setEmail(TEST_EMAIL);
        patientSignupInput.setPassword(TEST_PASSWORD);
        patientSignupInput.setConfirmPassword(TEST_PASSWORD);
        patientSignupInput.setPhoneNumber(TEST_PHONE);
        patientSignupInput.setAge(TEST_AGE);

        // Setup mock entities
        mockDoctor = new HospitalStaff();
        mockDoctor.setId(TEST_ID);
        mockDoctor.setName(TEST_NAME);
        mockDoctor.setEmail(TEST_EMAIL.toLowerCase());
        mockDoctor.setPassword(TEST_ENCODED_PASSWORD);
        mockDoctor.setRole("doctor");

        mockPatient = new Patient();
        mockPatient.setId(TEST_ID);
        mockPatient.setName(TEST_NAME);
        mockPatient.setEmail(TEST_EMAIL.toLowerCase());
        mockPatient.setPassword(TEST_ENCODED_PASSWORD);
        mockPatient.setPhoneNumber(TEST_PHONE);
        mockPatient.setAge(TEST_AGE);
        mockPatient.setRole("patient");
    }

    // Doctor Signup Tests

    @Test
    void testSignupDoctor_Success() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(doctorRepository.save(any(HospitalStaff.class))).thenReturn(mockDoctor);

        // Act
        SignupResponseDTO response = signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Doctor registered successfully! Please log in.", response.getMessage());
        assertEquals(TEST_ID, response.getUserId());
        assertEquals(TEST_EMAIL.toLowerCase(), response.getEmail());
        assertEquals("doctor", response.getRole());

        // Verify interactions
        verify(doctorRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verify(patientRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(doctorRepository).save(any(HospitalStaff.class));
    }

    @Test
    void testSignupDoctor_PasswordMismatch() {
        // Arrange
        doctorSignupInput.setConfirmPassword("differentPassword");

        // Act
        SignupResponseDTO response = signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Passwords do not match", response.getMessage());
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());

        // Verify no repository interactions
        verifyNoInteractions(doctorRepository, patientRepository, passwordEncoder);
    }

    @Test
    void testSignupDoctor_EmailExistsInDoctorRepository() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.of(mockDoctor));

        // Act
        SignupResponseDTO response = signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Email already registered", response.getMessage());
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());

        // Verify interactions
        verify(doctorRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verifyNoInteractions(patientRepository, passwordEncoder);
        verify(doctorRepository, never()).save(any(HospitalStaff.class));
    }

    @Test
    void testSignupDoctor_EmailExistsInPatientRepository() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(mockPatient));

        // Act
        SignupResponseDTO response = signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Email already registered", response.getMessage());
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());

        // Verify interactions
        verify(doctorRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verify(patientRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verifyNoInteractions(passwordEncoder);
        verify(doctorRepository, never()).save(any(HospitalStaff.class));
    }

    @Test
    void testSignupDoctor_RepositoryException() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(doctorRepository.save(any(HospitalStaff.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        SignupResponseDTO response = signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Registration failed"));
        assertTrue(response.getMessage().contains("Database error"));
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());
    }

    @Test
    void testSignupDoctor_InputTrimming() {
        // Arrange
        doctorSignupInput.setName("  " + TEST_NAME + "  ");
        doctorSignupInput.setEmail("  " + TEST_EMAIL.toUpperCase() + "  ");

        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(doctorRepository.save(any(HospitalStaff.class))).thenReturn(mockDoctor);

        // Act
        SignupResponseDTO response = signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        assertTrue(response.isSuccess());

        // Verify the doctor was saved with trimmed and lowercase email
        verify(doctorRepository).save(argThat(doctor ->
                doctor.getName().equals(TEST_NAME) &&
                        doctor.getEmail().equals(TEST_EMAIL.toLowerCase())
        ));
    }

    // Patient Signup Tests

    @Test
    void testSignupPatient_Success() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);

        // Act
        SignupResponseDTO response = signupResolver.signupPatient(patientSignupInput);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Patient registered successfully! Please log in.", response.getMessage());
        assertEquals(TEST_ID, response.getUserId());
        assertEquals(TEST_EMAIL.toLowerCase(), response.getEmail());
        assertEquals("patient", response.getRole());

        // Verify interactions
        verify(doctorRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verify(patientRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testSignupPatient_PasswordMismatch() {
        // Arrange
        patientSignupInput.setConfirmPassword("differentPassword");

        // Act
        SignupResponseDTO response = signupResolver.signupPatient(patientSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Passwords do not match", response.getMessage());
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());

        // Verify no repository interactions
        verifyNoInteractions(doctorRepository, patientRepository, passwordEncoder);
    }

    @Test
    void testSignupPatient_EmailExistsInDoctorRepository() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.of(mockDoctor));

        // Act
        SignupResponseDTO response = signupResolver.signupPatient(patientSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Email already registered", response.getMessage());
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());

        // Verify interactions
        verify(doctorRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verifyNoInteractions(patientRepository, passwordEncoder);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testSignupPatient_EmailExistsInPatientRepository() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.of(mockPatient));

        // Act
        SignupResponseDTO response = signupResolver.signupPatient(patientSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Email already registered", response.getMessage());
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());

        // Verify interactions
        verify(doctorRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verify(patientRepository).findByEmail(TEST_EMAIL.toLowerCase());
        verifyNoInteractions(passwordEncoder);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testSignupPatient_RepositoryException() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(patientRepository.save(any(Patient.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        SignupResponseDTO response = signupResolver.signupPatient(patientSignupInput);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Registration failed"));
        assertTrue(response.getMessage().contains("Database error"));
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getRole());
    }

    @Test
    void testSignupPatient_InputTrimming() {
        // Arrange
        patientSignupInput.setName("  " + TEST_NAME + "  ");
        patientSignupInput.setEmail("  " + TEST_EMAIL.toUpperCase() + "  ");
        patientSignupInput.setPhoneNumber("  " + TEST_PHONE + "  ");

        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);

        // Act
        SignupResponseDTO response = signupResolver.signupPatient(patientSignupInput);

        // Assert
        assertTrue(response.isSuccess());

        // Verify the patient was saved with trimmed fields
        verify(patientRepository).save(argThat(patient ->
                patient.getName().equals(TEST_NAME) &&
                        patient.getEmail().equals(TEST_EMAIL.toLowerCase()) &&
                        patient.getPhoneNumber().equals(TEST_PHONE)
        ));
    }

    @Test
    void testSignupPatient_AllFieldsSet() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);

        // Act
        SignupResponseDTO response = signupResolver.signupPatient(patientSignupInput);

        // Assert
        assertTrue(response.isSuccess());

        // Verify all patient fields are set correctly
        verify(patientRepository).save(argThat(patient ->
                patient.getName().equals(TEST_NAME) &&
                        patient.getEmail().equals(TEST_EMAIL.toLowerCase()) &&
                        patient.getPhoneNumber().equals(TEST_PHONE) &&
                        patient.getAge().equals(TEST_AGE) &&
                        patient.getRole().equals("patient") &&
                        patient.getPassword().equals(TEST_ENCODED_PASSWORD)
        ));
    }

    @Test
    void testSignupDoctor_AllFieldsSet() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(doctorRepository.save(any(HospitalStaff.class))).thenReturn(mockDoctor);

        // Act
        SignupResponseDTO response = signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        assertTrue(response.isSuccess());

        // Verify all doctor fields are set correctly
        verify(doctorRepository).save(argThat(doctor ->
                doctor.getName().equals(TEST_NAME) &&
                        doctor.getEmail().equals(TEST_EMAIL.toLowerCase()) &&
                        doctor.getRole().equals("doctor") &&
                        doctor.getPassword().equals(TEST_ENCODED_PASSWORD)
        ));
    }

    @Test
    void testSignupDoctor_PasswordEncodingVerification() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(doctorRepository.save(any(HospitalStaff.class))).thenReturn(mockDoctor);

        // Act
        signupResolver.signupDoctor(doctorSignupInput);

        // Assert
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(doctorRepository).save(argThat(doctor ->
                doctor.getPassword().equals(TEST_ENCODED_PASSWORD)
        ));
    }

    @Test
    void testSignupPatient_PasswordEncodingVerification() {

        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);
        signupResolver.signupPatient(patientSignupInput);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(patientRepository).save(argThat(patient ->
                patient.getPassword().equals(TEST_ENCODED_PASSWORD)
        ));
    }
}