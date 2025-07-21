package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.AppointmentInputDTO;
import com.scheduler.schedulerapp.dto.AppointmentUpdateInputDTO;
import com.scheduler.schedulerapp.dto.AppointmentResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.model.Doctor;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.service.appointment.AppointmentService;
import com.scheduler.schedulerapp.service.doctor.DoctorService;
import com.scheduler.schedulerapp.service.patient.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
@WithMockUser(roles = "ADMIN")
class AppointmentResolverTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PatientService patientService;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private AppointmentResolver appointmentResolver;

    private Appointment testAppointment;
    private AppointmentResponseDTO testResponseDTO;
    private Doctor testDoctor;
    private Patient testPatient;
    private String adminId;
    private String doctorId;
    private String patientId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        adminId = "68760865c47985c69cf4c4c1";
        doctorId = "doctor123";
        patientId = "patient123";
        startTime = LocalDateTime.of(2025, 8, 15, 10, 0);
        endTime = LocalDateTime.of(2025, 8, 15, 11, 0);

        testDoctor = new Doctor();
        testDoctor.setId(doctorId);
        testDoctor.setName("Test Doctor");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setRole("doctor");

        testPatient = new Patient();
        testPatient.setId(patientId);
        testPatient.setName("Test Patient");
        testPatient.setEmail("patient@test.com");
        testPatient.setRole("patient");

        testAppointment = new Appointment();
        testAppointment.setId("appointment123");
        testAppointment.setTitle("Test Appointment");
        testAppointment.setDoctorId(doctorId);
        testAppointment.setPatientId(patientId);
        testAppointment.setStartTime(startTime);
        testAppointment.setEndTime(endTime);
        testAppointment.setStatus("scheduled");

        testResponseDTO = new AppointmentResponseDTO();
        testResponseDTO.setId("appointment123");
        testResponseDTO.setTitle("Test Appointment");
        testResponseDTO.setDoctorId(doctorId);
        testResponseDTO.setPatientId(patientId);
    }

    private AppointmentInputDTO createValidAppointmentInput() {
        AppointmentInputDTO input = new AppointmentInputDTO();
        input.setTitle("Test Appointment");
        input.setDoctorId(doctorId);
        input.setPatientId(patientId);
        input.setStartTime("2025-08-15T10:00:00");
        input.setEndTime("2025-08-15T11:00:00");
        input.setStatus("scheduled");
        return input;
    }

    private AppointmentUpdateInputDTO createValidUpdateInput() {
        AppointmentUpdateInputDTO input = new AppointmentUpdateInputDTO();
        input.setTitle("Updated Appointment");
        input.setStartTime("2025-08-15T14:00:00");
        input.setEndTime("2025-08-15T15:00:00");
        input.setDescription("Updated appointment description");
        input.setStatus("scheduled");
        return input;
    }

    // appointments Tests

    @Test
    void appointments_AdminAccessWithValidAdminId_ReturnsAllAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        List<AppointmentResponseDTO> expectedDTOs = List.of(testResponseDTO);
        when(appointmentService.getAllAppointments()).thenReturn(appointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointments(adminId);

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAllAppointments();
    }

    @Test
    void appointments_NonAdminAccess_ThrowsSecurityException() {
        String nonAdminId = "nonAdmin123";

        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentResolver.appointments(nonAdminId));

        assertEquals("Access denied: Only admin can view all appointments", exception.getMessage());
        verify(appointmentService, never()).getAllAppointments();
    }

    @Test
    void appointments_InvalidAdminId_ThrowsSecurityException() {
        String invalidAdminId = "invalid123";

        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentResolver.appointments(invalidAdminId));

        assertEquals("Access denied: Only admin can view all appointments", exception.getMessage());
        verify(appointmentService, never()).getAllAppointments();
    }

    // appointmentsByDoctor Tests

    @Test
    void appointmentsByDoctor_ValidDoctorIdWithAppointments_ReturnsDoctorAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(appointmentService.getAppointmentsByDoctor(doctorId)).thenReturn(appointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByDoctor(doctorId);

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByDoctor(doctorId);
    }

    @Test
    void appointmentsByDoctor_InvalidDoctorId_ThrowsIllegalArgumentException() {
        String invalidDoctorId = "invalid123";
        when(doctorService.getDoctorById(invalidDoctorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.appointmentsByDoctor(invalidDoctorId));

        assertEquals("Doctor not found with ID: " + invalidDoctorId, exception.getMessage());
        verify(appointmentService, never()).getAppointmentsByDoctor(any());
    }

    // appointmentsByPatient Tests

    @Test
    void appointmentsByPatient_ValidPatientIdWithAppointments_ReturnsPatientAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));
        when(appointmentService.getAppointmentsByPatient(patientId)).thenReturn(appointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByPatient(patientId);

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByPatient(patientId);
    }

    @Test
    void appointmentsByPatient_InvalidPatientId_ThrowsIllegalArgumentException() {
        String invalidPatientId = "invalid123";
        when(patientService.getPatientById(invalidPatientId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.appointmentsByPatient(invalidPatientId));

        assertEquals("Patient not found with ID: " + invalidPatientId, exception.getMessage());
        verify(appointmentService, never()).getAppointmentsByPatient(any());
    }

    // appointmentById Tests

    @Test
    void appointmentById_AdminAccessToAnyAppointment_ReturnsAppointment() {
        when(appointmentService.getAppointmentById("appointment123")).thenReturn(Optional.of(testAppointment));
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.appointmentById("appointment123", adminId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).getAppointmentById("appointment123");
    }

    @Test
    void appointmentById_DoctorAccessToOwnAppointment_ReturnsAppointment() {
        when(appointmentService.getAppointmentById("appointment123")).thenReturn(Optional.of(testAppointment));
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.appointmentById("appointment123", doctorId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).getAppointmentById("appointment123");
    }

    @Test
    void appointmentById_PatientAccessToOwnAppointment_ReturnsAppointment() {
        when(appointmentService.getAppointmentById("appointment123")).thenReturn(Optional.of(testAppointment));
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.appointmentById("appointment123", patientId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).getAppointmentById("appointment123");
    }

    @Test
    void appointmentById_DoctorAccessToOtherAppointment_ReturnsNull() {
        Appointment otherAppointment = new Appointment();
        otherAppointment.setId("appointment123");
        otherAppointment.setDoctorId("otherDoctor123");
        when(appointmentService.getAppointmentById("appointment123")).thenReturn(Optional.of(otherAppointment));

        AppointmentResponseDTO result = appointmentResolver.appointmentById("appointment123", doctorId);

        assertNull(result);
        verify(appointmentService).getAppointmentById("appointment123");
    }

    @Test
    void appointmentById_NonExistentAppointmentId_ReturnsNull() {
        when(appointmentService.getAppointmentById("nonexistent")).thenReturn(Optional.empty());

        AppointmentResponseDTO result = appointmentResolver.appointmentById("nonexistent", adminId);

        assertNull(result);
        verify(appointmentService).getAppointmentById("nonexistent");
    }

    @Test
    void appointmentById_InvalidRequesterId_ReturnsNull() {
        Appointment otherAppointment = new Appointment();
        otherAppointment.setId("appointment123");
        otherAppointment.setDoctorId("otherDoctor123");
        when(appointmentService.getAppointmentById("appointment123")).thenReturn(Optional.of(otherAppointment));

        AppointmentResponseDTO result = appointmentResolver.appointmentById("appointment123", "invalidUser");

        assertNull(result);
        verify(appointmentService).getAppointmentById("appointment123");
    }

    // appointmentsByDateRange Tests

    @Test
    void appointmentsByDateRange_AdminAccessWithValidDateRange_ReturnsAppointmentsInRange() {
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentService.getAppointmentsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(appointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByDateRange(
                adminId, "2025-01-15T00:00:00", "2025-01-15T23:59:59");

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void appointmentsByDateRange_NonAdminAccess_ThrowsSecurityException() {
        String nonAdminId = "nonAdmin123";

        SecurityException exception = assertThrows(SecurityException.class, () -> appointmentResolver
                .appointmentsByDateRange(nonAdminId, "2025-01-15T00:00:00", "2025-01-15T23:59:59"));

        assertEquals("Access denied: Only admin can view all appointments", exception.getMessage());
        verify(appointmentService, never()).getAppointmentsByDateRange(any(), any());
    }

    @Test
    void appointmentsByDateRange_InvalidDateFormat_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.appointmentsByDateRange(adminId, "invalid-date", "2025-01-15T23:59:59"));

        verify(appointmentService, never()).getAppointmentsByDateRange(any(), any());
    }

    // appointmentsByDoctorAndDateRange Tests

    @Test
    void appointmentsByDoctorAndDateRange_ValidDoctorAndDateRange_ReturnsFilteredAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(appointmentService.getAppointmentsByDoctorAndDateRange(eq(doctorId), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(appointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByDoctorAndDateRange(
                doctorId, "2025-01-15T00:00:00", "2025-01-15T23:59:59");

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByDoctorAndDateRange(eq(doctorId), any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

    @Test
    void appointmentsByDoctorAndDateRange_InvalidDoctorId_ThrowsIllegalArgumentException() {
        String invalidDoctorId = "invalid123";
        when(doctorService.getDoctorById(invalidDoctorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentResolver
                .appointmentsByDoctorAndDateRange(invalidDoctorId, "2025-01-15T00:00:00", "2025-01-15T23:59:59"));

        assertEquals("Doctor not found with ID: " + invalidDoctorId, exception.getMessage());
        verify(appointmentService, never()).getAppointmentsByDoctorAndDateRange(any(), any(), any());
    }

    @Test
    void appointmentsByDoctorAndDateRange_InvalidDateFormat_ThrowsIllegalArgumentException() {
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));

        assertThrows(IllegalArgumentException.class, () -> appointmentResolver
                .appointmentsByDoctorAndDateRange(doctorId, "invalid-date", "2025-01-15T23:59:59"));

        verify(appointmentService, never()).getAppointmentsByDoctorAndDateRange(any(), any(), any());
    }

    // appointmentsByPatientAndDateRange Tests

    @Test
    void appointmentsByPatientAndDateRange_ValidPatientAndDateRange_ReturnsFilteredAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));
        when(appointmentService.getAppointmentsByPatientAndDateRange(eq(patientId), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(appointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByPatientAndDateRange(
                patientId, "2025-01-15T00:00:00", "2025-01-15T23:59:59");

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByPatientAndDateRange(eq(patientId), any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

    @Test
    void appointmentsByPatientAndDateRange_InvalidPatientId_ThrowsIllegalArgumentException() {
        String invalidPatientId = "invalid123";
        when(patientService.getPatientById(invalidPatientId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentResolver
                .appointmentsByPatientAndDateRange(invalidPatientId, "2025-01-15T00:00:00", "2025-01-15T23:59:59"));

        assertEquals("Patient not found with ID: " + invalidPatientId, exception.getMessage());
        verify(appointmentService, never()).getAppointmentsByPatientAndDateRange(any(), any(), any());
    }

    @Test
    void appointmentsByPatientAndDateRange_InvalidDateFormat_ThrowsIllegalArgumentException() {
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));

        assertThrows(IllegalArgumentException.class, () -> appointmentResolver
                .appointmentsByPatientAndDateRange(patientId, "invalid-date", "2025-01-15T23:59:59"));

        verify(appointmentService, never()).getAppointmentsByPatientAndDateRange(any(), any(), any());
    }

    // appointmentsByStatus Tests

    @Test
    void appointmentsByStatus_AdminAccessToAppointmentsByStatus_ReturnsAllMatchingAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentService.getAppointmentsByStatus("scheduled")).thenReturn(appointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByStatus("scheduled", adminId);

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByStatus("scheduled");
    }

    @Test
    void appointmentsByStatus_DoctorAccessToOwnAppointmentsByStatus_ReturnsFilteredAppointments() {
        List<Appointment> doctorAppointments = List.of(testAppointment);
        when(appointmentService.getAppointmentsByDoctor(doctorId)).thenReturn(doctorAppointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByStatus("scheduled", doctorId);

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByDoctor(doctorId);
    }

    @Test
    void appointmentsByStatus_PatientAccessToOwnAppointmentsByStatus_ReturnsFilteredAppointments() {
        List<Appointment> patientAppointments = List.of(testAppointment);
        when(appointmentService.getAppointmentsByDoctor(patientId)).thenReturn(patientAppointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByStatus("scheduled", patientId);

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).getAppointmentsByDoctor(patientId);
    }

    @Test
    void appointmentsByStatus_InvalidRequesterId_ReturnsOnlyRequesterAppointments() {
        String invalidRequesterId = "invalid123";
        List<Appointment> userAppointments = List.of(testAppointment);
        when(appointmentService.getAppointmentsByDoctor(invalidRequesterId)).thenReturn(userAppointments);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.appointmentsByStatus("scheduled", invalidRequesterId);

        assertEquals(1, result.size());
        verify(appointmentService).getAppointmentsByDoctor(invalidRequesterId);
    }

    // checkCollision Tests

    @Test
    void checkCollision_ValidIdsWithNoConflicts_ReturnsEmptyList() {
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));
        when(appointmentService.checkCollision(eq(doctorId), eq(patientId), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<AppointmentResponseDTO> result = appointmentResolver.checkCollision(
                doctorId, patientId, "2025-01-15T10:00:00", "2025-01-15T11:00:00");

        assertTrue(result.isEmpty());
        verify(appointmentService).checkCollision(eq(doctorId), eq(patientId), any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

    @Test
    void checkCollision_ValidIdsWithConflicts_ReturnsConflictingAppointments() {
        List<Appointment> conflicts = List.of(testAppointment);
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));
        when(appointmentService.checkCollision(eq(doctorId), eq(patientId), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(conflicts);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        List<AppointmentResponseDTO> result = appointmentResolver.checkCollision(
                doctorId, patientId, "2025-01-15T10:00:00", "2025-01-15T11:00:00");

        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(appointmentService).checkCollision(eq(doctorId), eq(patientId), any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

    @Test
    void checkCollision_InvalidDoctorId_ThrowsIllegalArgumentException() {
        String invalidDoctorId = "invalid123";
        when(doctorService.getDoctorById(invalidDoctorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentResolver
                .checkCollision(invalidDoctorId, patientId, "2025-01-15T10:00:00", "2025-01-15T11:00:00"));

        assertEquals("Doctor not found with ID: " + invalidDoctorId, exception.getMessage());
        verify(appointmentService, never()).checkCollision(any(), any(), any(), any());
    }

    @Test
    void checkCollision_InvalidPatientId_ThrowsIllegalArgumentException() {
        String invalidPatientId = "invalid123";
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(invalidPatientId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appointmentResolver
                .checkCollision(doctorId, invalidPatientId, "2025-01-15T10:00:00", "2025-01-15T11:00:00"));

        assertEquals("Patient not found with ID: " + invalidPatientId, exception.getMessage());
        verify(appointmentService, never()).checkCollision(any(), any(), any(), any());
    }

    @Test
    void checkCollision_InvalidDateFormat_ThrowsIllegalArgumentException() {
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));

        assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.checkCollision(doctorId, patientId, "invalid-date", "2025-01-15T11:00:00"));

        verify(appointmentService, never()).checkCollision(any(), any(), any(), any());
    }

    // createAppointment Tests

    @Test
    void createAppointment_ValidInputCreatesAppointment_ReturnsCreatedAppointment() {
        AppointmentInputDTO input = createValidAppointmentInput();
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.createAppointment(input);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).createAppointment(any(Appointment.class));
    }

    @Test
    void createAppointment_InvalidDoctorId_ThrowsIllegalArgumentException() {
        AppointmentInputDTO input = createValidAppointmentInput();
        input.setDoctorId("invalid123");
        when(doctorService.getDoctorById("invalid123")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.createAppointment(input));

        assertEquals("Doctor not found with ID: invalid123", exception.getMessage());
        verify(appointmentService, never()).createAppointment(any());
    }

    @Test
    void createAppointment_InvalidPatientId_ThrowsIllegalArgumentException() {
        AppointmentInputDTO input = createValidAppointmentInput();
        input.setPatientId("invalid123");
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById("invalid123")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.createAppointment(input));

        assertEquals("Patient not found with ID: invalid123", exception.getMessage());
        verify(appointmentService, never()).createAppointment(any());
    }

    @Test
    void createAppointment_PastAppointmentTime_ThrowsIllegalArgumentException() {
        AppointmentInputDTO input = createValidAppointmentInput();
        input.setStartTime("2020-01-15T10:00:00");
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.createAppointment(input));

        assertEquals("Cannot create appointments in the past", exception.getMessage());
        verify(appointmentService, never()).createAppointment(any());
    }

    @Test
    void createAppointment_ServiceThrowsConflictException_PropagatesException() {
        AppointmentInputDTO input = createValidAppointmentInput();
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));
        when(appointmentService.createAppointment(any(Appointment.class)))
                .thenThrow(new RuntimeException("Scheduling conflict"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> appointmentResolver.createAppointment(input));

        assertEquals("Scheduling conflict", exception.getMessage());
        verify(appointmentService).createAppointment(any(Appointment.class));
    }

    @Test
    void createAppointment_WithNullStatus_UsesDefaultStatus() {
        AppointmentInputDTO input = createValidAppointmentInput();
        input.setStatus(null); // Explicitly set status to null
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(testPatient));
        when(appointmentService.createAppointment(any(Appointment.class))).thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.createAppointment(input);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).createAppointment(any(Appointment.class));

        // Verify that the appointment was created with default status "scheduled"
        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentService).createAppointment(appointmentCaptor.capture());
        Appointment capturedAppointment = appointmentCaptor.getValue();
        assertEquals("scheduled", capturedAppointment.getStatus());
    }

    // updateAppointment Tests

    @Test
    void updateAppointment_AdminUpdatesAnyAppointment_ReturnsUpdatedAppointment() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, adminId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_DoctorUpdatesOwnAppointment_ReturnsUpdatedAppointment() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, doctorId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_PatientUpdatesOwnAppointment_ReturnsUpdatedAppointment() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, patientId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_DoctorTriesToUpdateOtherAppointment_ThrowsSecurityException() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        Appointment otherAppointment = new Appointment();
        otherAppointment.setId(appointmentId);
        otherAppointment.setDoctorId("otherDoctor123");
        otherAppointment.setPatientId("otherPatient123");
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(otherAppointment));

        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentResolver.updateAppointment(appointmentId, input, doctorId));

        assertEquals("Access denied: You can only update your own appointments", exception.getMessage());
        verify(appointmentService, never()).updateAppointment(any(), any());
    }

    @Test
    void updateAppointment_NonExistentAppointment_ThrowsIllegalArgumentException() {
        String appointmentId = "nonexistent123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.updateAppointment(appointmentId, input, adminId));

        assertEquals("Appointment not found with ID: " + appointmentId, exception.getMessage());
        verify(appointmentService, never()).updateAppointment(any(), any());
    }

    @Test
    void updateAppointment_UnauthorizedUser_ThrowsSecurityException() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        Appointment otherAppointment = new Appointment();
        otherAppointment.setId(appointmentId);
        otherAppointment.setDoctorId("otherDoctor123");
        otherAppointment.setPatientId("otherPatient123");
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(otherAppointment));

        String unauthorizedUser = "unauthorized123";

        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentResolver.updateAppointment(appointmentId, input, unauthorizedUser));

        assertEquals("Access denied: You can only update your own appointments", exception.getMessage());
        verify(appointmentService, never()).updateAppointment(any(), any());
    }

    @Test
    void updateAppointment_WithNullTitle_DoesNotUpdateTitle() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = new AppointmentUpdateInputDTO();
        input.setTitle(null);
        input.setStartTime("2025-12-15T14:00:00");
        input.setEndTime("2025-12-15T15:00:00");

        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, adminId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_WithNullDescription_DoesNotUpdateDescription() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = new AppointmentUpdateInputDTO();
        input.setDescription(null);
        input.setStartTime("2025-12-15T14:00:00");
        input.setEndTime("2025-12-15T15:00:00");

        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, adminId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_WithNullStartTime_DoesNotUpdateStartTime() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = new AppointmentUpdateInputDTO();
        input.setStartTime(null);
        input.setEndTime("2025-12-15T15:00:00");

        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, adminId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_WithNullEndTime_DoesNotUpdateEndTime() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = new AppointmentUpdateInputDTO();
        input.setStartTime("2025-12-15T14:00:00");
        input.setEndTime(null);

        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, adminId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_WithNullStatus_DoesNotUpdateStatus() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = new AppointmentUpdateInputDTO();
        input.setStartTime("2025-12-15T14:00:00");
        input.setEndTime("2025-12-15T15:00:00");
        input.setStatus(null);

        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenReturn(testAppointment);
        when(dtoMapper.toAppointmentResponseDTO(testAppointment)).thenReturn(testResponseDTO);

        AppointmentResponseDTO result = appointmentResolver.updateAppointment(appointmentId, input, adminId);

        assertEquals(testResponseDTO, result);
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    @Test
    void updateAppointment_PastTimeUpdate_ThrowsIllegalArgumentException() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        input.setStartTime("2020-01-15T10:00:00");
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.updateAppointment(appointmentId, input, adminId));

        assertEquals("Cannot update appointment to past time", exception.getMessage());
        verify(appointmentService, never()).updateAppointment(any(), any());
    }

    @Test
    void updateAppointment_ServiceThrowsException_PropagatesException() {
        String appointmentId = "appointment123";
        AppointmentUpdateInputDTO input = createValidUpdateInput();
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentService.updateAppointment(eq(appointmentId), any(Appointment.class)))
                .thenThrow(new RuntimeException("Update failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> appointmentResolver.updateAppointment(appointmentId, input, adminId));

        assertEquals("Update failed", exception.getMessage());
        verify(appointmentService).updateAppointment(eq(appointmentId), any(Appointment.class));
    }

    // deleteAppointment Tests

    @Test
    void deleteAppointment_AdminDeletesAnyAppointment_ReturnsTrue() {
        String appointmentId = "appointment123";
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        doNothing().when(appointmentService).deleteAppointment(appointmentId);

        Boolean result = appointmentResolver.deleteAppointment(appointmentId, adminId);

        assertTrue(result);
        verify(appointmentService).deleteAppointment(appointmentId);
    }

    @Test
    void deleteAppointment_DoctorDeletesOwnAppointment_ReturnsTrue() {
        String appointmentId = "appointment123";
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        doNothing().when(appointmentService).deleteAppointment(appointmentId);

        Boolean result = appointmentResolver.deleteAppointment(appointmentId, doctorId);

        assertTrue(result);
        verify(appointmentService).deleteAppointment(appointmentId);
    }

    @Test
    void deleteAppointment_PatientDeletesOwnAppointment_ReturnsTrue() {
        String appointmentId = "appointment123";
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(testAppointment));
        doNothing().when(appointmentService).deleteAppointment(appointmentId);

        Boolean result = appointmentResolver.deleteAppointment(appointmentId, patientId);

        assertTrue(result);
        verify(appointmentService).deleteAppointment(appointmentId);
    }

    @Test
    void deleteAppointment_DoctorTriesToDeleteOtherAppointment_ThrowsSecurityException() {
        String appointmentId = "appointment123";
        Appointment otherAppointment = new Appointment();
        otherAppointment.setId(appointmentId);
        otherAppointment.setDoctorId("otherDoctor123");
        otherAppointment.setPatientId("otherPatient123");
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.of(otherAppointment));

        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentResolver.deleteAppointment(appointmentId, doctorId));

        assertEquals("Access denied: You can only delete your own appointments", exception.getMessage());
        verify(appointmentService, never()).deleteAppointment(any());
    }

    @Test
    void deleteAppointment_NonExistentAppointment_ThrowsIllegalArgumentException() {
        String appointmentId = "nonexistent123";
        when(appointmentService.getAppointmentById(appointmentId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.deleteAppointment(appointmentId, adminId));

        assertEquals("Appointment not found with ID: " + appointmentId, exception.getMessage());
        verify(appointmentService, never()).deleteAppointment(any());
    }

    // deleteMultipleAppointments Tests

    @Test
    void deleteMultipleAppointments_AdminDeletesMultipleAppointments_ReturnsTrue() {
        List<String> appointmentIds = List.of("appointment1", "appointment2");
        Appointment appointment1 = new Appointment();
        appointment1.setId("appointment1");
        appointment1.setDoctorId(doctorId);
        Appointment appointment2 = new Appointment();
        appointment2.setId("appointment2");
        appointment2.setDoctorId(doctorId);

        when(appointmentService.getAppointmentById("appointment1")).thenReturn(Optional.of(appointment1));
        when(appointmentService.getAppointmentById("appointment2")).thenReturn(Optional.of(appointment2));
        doNothing().when(appointmentService).deleteMultipleAppointments(appointmentIds);

        Boolean result = appointmentResolver.deleteMultipleAppointments(appointmentIds, adminId);

        assertTrue(result);
        verify(appointmentService).deleteMultipleAppointments(appointmentIds);
    }

    @Test
    void deleteMultipleAppointments_DoctorDeletesOwnAppointments_ReturnsTrue() {
        List<String> appointmentIds = List.of("appointment1", "appointment2");
        Appointment appointment1 = new Appointment();
        appointment1.setId("appointment1");
        appointment1.setDoctorId(doctorId);
        Appointment appointment2 = new Appointment();
        appointment2.setId("appointment2");
        appointment2.setDoctorId(doctorId);

        when(appointmentService.getAppointmentById("appointment1")).thenReturn(Optional.of(appointment1));
        when(appointmentService.getAppointmentById("appointment2")).thenReturn(Optional.of(appointment2));
        doNothing().when(appointmentService).deleteMultipleAppointments(appointmentIds);

        Boolean result = appointmentResolver.deleteMultipleAppointments(appointmentIds, doctorId);

        assertTrue(result);
        verify(appointmentService).deleteMultipleAppointments(appointmentIds);
    }

    @Test
    void deleteMultipleAppointments_PatientDeletesOwnAppointments_ReturnsTrue() {
        List<String> appointmentIds = List.of("appointment1", "appointment2");
        Appointment appointment1 = new Appointment();
        appointment1.setId("appointment1");
        appointment1.setDoctorId("someDoctor");
        appointment1.setPatientId(patientId);
        Appointment appointment2 = new Appointment();
        appointment2.setId("appointment2");
        appointment2.setDoctorId("someDoctor");
        appointment2.setPatientId(patientId);

        when(appointmentService.getAppointmentById("appointment1")).thenReturn(Optional.of(appointment1));
        when(appointmentService.getAppointmentById("appointment2")).thenReturn(Optional.of(appointment2));
        doNothing().when(appointmentService).deleteMultipleAppointments(appointmentIds);

        Boolean result = appointmentResolver.deleteMultipleAppointments(appointmentIds, patientId);

        assertTrue(result);
        verify(appointmentService).deleteMultipleAppointments(appointmentIds);
    }

    @Test
    void deleteMultipleAppointments_DoctorTriesToDeleteOtherAppointments_ThrowsSecurityException() {
        List<String> appointmentIds = List.of("appointment1", "appointment2");
        Appointment otherAppointment = new Appointment();
        otherAppointment.setId("appointment1");
        otherAppointment.setDoctorId("otherDoctor123");
        otherAppointment.setPatientId("otherPatient123");

        when(appointmentService.getAppointmentById("appointment1")).thenReturn(Optional.of(otherAppointment));

        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentResolver.deleteMultipleAppointments(appointmentIds, doctorId));

        assertEquals("Access denied: You can only delete your own appointments", exception.getMessage());
        verify(appointmentService, never()).deleteMultipleAppointments(any());
    }

    @Test
    void deleteMultipleAppointments_SomeNonExistentIds_ThrowsIllegalArgumentException() {
        List<String> appointmentIds = List.of("appointment1", "nonexistent");
        when(appointmentService.getAppointmentById("appointment1")).thenReturn(Optional.of(testAppointment));
        when(appointmentService.getAppointmentById("nonexistent")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentResolver.deleteMultipleAppointments(appointmentIds, adminId));

        assertEquals("Appointment not found with ID: nonexistent", exception.getMessage());
        verify(appointmentService, never()).deleteMultipleAppointments(any());
    }
}