package com.scheduler.schedulerapp.service;

import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.repository.AppointmentRepository;
import com.scheduler.schedulerapp.service.appointment.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment testAppointment;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String doctorId;
    private String patientId;

    @BeforeEach
    void setUp() {
        doctorId = "doctor123";
        patientId = "patient123";
        startTime = LocalDateTime.of(2025, 1, 15, 10, 0);
        endTime = LocalDateTime.of(2025, 1, 15, 11, 0);

        testAppointment = new Appointment();
        testAppointment.setId("appointment123");
        testAppointment.setTitle("Test Appointment");
        testAppointment.setDescription("This is a test appointment.");
        testAppointment.setDoctorId(doctorId);
        testAppointment.setPatientId(patientId);
        testAppointment.setStartTime(startTime);
        testAppointment.setEndTime(endTime);
        testAppointment.setStatus("scheduled");
    }

    private Appointment createConflictAppointment(String id) {
        Appointment conflict = new Appointment();
        conflict.setId(id);
        conflict.setTitle("Conflicting Appointment");
        conflict.setDescription("This appointment conflicts with another.");
        conflict.setDoctorId(doctorId);
        conflict.setPatientId(patientId);
        conflict.setStartTime(startTime);
        conflict.setEndTime(endTime);
        conflict.setStatus("scheduled");

        return conflict;
    }

    // checkCollision Tests

    @Test
    void checkCollision_NoConflicts_ReturnsEmptyList() {
        when(appointmentRepository.findDoctorCollision(doctorId, startTime, endTime))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(patientId, startTime, endTime))
                .thenReturn(Collections.emptyList());

        List<Appointment> conflicts = appointmentService.checkCollision(doctorId, patientId, startTime, endTime);

        assertTrue(conflicts.isEmpty());
        verify(appointmentRepository).findDoctorCollision(doctorId, startTime, endTime);
        verify(appointmentRepository).findPatientCollision(patientId, startTime, endTime);
    }

    @Test
    void checkCollision_DoctorHasConflict_ReturnsConflictList() {
        Appointment doctorConflict = createConflictAppointment("doctorConflict");
        when(appointmentRepository.findDoctorCollision(doctorId, startTime, endTime))
                .thenReturn(List.of(doctorConflict));
        when(appointmentRepository.findPatientCollision(patientId, startTime, endTime))
                .thenReturn(Collections.emptyList());

        List<Appointment> conflicts = appointmentService.checkCollision(doctorId, patientId, startTime, endTime);

        assertEquals(1, conflicts.size());
        assertEquals("doctorConflict", conflicts.getFirst().getId());
        verify(appointmentRepository).findDoctorCollision(doctorId, startTime, endTime);
        verify(appointmentRepository).findPatientCollision(patientId, startTime, endTime);
    }

    @Test
    void checkCollision_PatientHasConflict_ReturnsConflictList() {
        Appointment patientConflict = createConflictAppointment("patientConflict");
        when(appointmentRepository.findDoctorCollision(doctorId, startTime, endTime))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(patientId, startTime, endTime))
                .thenReturn(List.of(patientConflict));

        List<Appointment> conflicts = appointmentService.checkCollision(doctorId, patientId, startTime, endTime);

        assertEquals(1, conflicts.size());
        assertEquals("patientConflict", conflicts.getFirst().getId());
        verify(appointmentRepository).findDoctorCollision(doctorId, startTime, endTime);
        verify(appointmentRepository).findPatientCollision(patientId, startTime, endTime);
    }

    @Test
    void checkCollision_BothHaveConflicts_ReturnsCombinedConflictList() {
        Appointment doctorConflict = createConflictAppointment("doctorConflict");
        Appointment patientConflict = createConflictAppointment("patientConflict");
        when(appointmentRepository.findDoctorCollision(doctorId, startTime, endTime))
                .thenReturn(List.of(doctorConflict));
        when(appointmentRepository.findPatientCollision(patientId, startTime, endTime))
                .thenReturn(List.of(patientConflict));

        List<Appointment> conflicts = appointmentService.checkCollision(doctorId, patientId, startTime, endTime);

        assertEquals(2, conflicts.size());
        assertTrue(conflicts.stream().anyMatch(c -> c.getId().equals("doctorConflict")));
        assertTrue(conflicts.stream().anyMatch(c -> c.getId().equals("patientConflict")));
    }

    @Test
    void checkCollision_WrongDoctorId_ReturnsEmptyList() {
        String wrongDoctorId = "wrongDoctor";
        when(appointmentRepository.findDoctorCollision(wrongDoctorId, startTime, endTime))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(patientId, startTime, endTime))
                .thenReturn(Collections.emptyList());

        List<Appointment> conflicts = appointmentService.checkCollision(wrongDoctorId, patientId, startTime, endTime);

        assertTrue(conflicts.isEmpty());
        verify(appointmentRepository).findDoctorCollision(wrongDoctorId, startTime, endTime);
        verify(appointmentRepository).findPatientCollision(patientId, startTime, endTime);
    }

    @Test
    void checkCollision_WrongPatientId_ReturnsEmptyList() {
        String wrongPatientId = "wrongPatient";
        when(appointmentRepository.findDoctorCollision(doctorId, startTime, endTime))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(wrongPatientId, startTime, endTime))
                .thenReturn(Collections.emptyList());

        List<Appointment> conflicts = appointmentService.checkCollision(doctorId, wrongPatientId, startTime, endTime);

        assertTrue(conflicts.isEmpty());
        verify(appointmentRepository).findDoctorCollision(doctorId, startTime, endTime);
        verify(appointmentRepository).findPatientCollision(wrongPatientId, startTime, endTime);
    }

    // createAppointment Tests

    @Test
    void createAppointment_SuccessfulCreation_ReturnsAppointment() {
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.createAppointment(testAppointment);

        assertNotNull(result);
        assertEquals("appointment123", result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void createAppointment_ConflictExists_ThrowsRuntimeException() {
        Appointment conflictAppointment = createConflictAppointment("conflict123");
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(List.of(conflictAppointment));
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                appointmentService.createAppointment(testAppointment));

        assertTrue(exception.getMessage().contains("Conflicting Appointment"));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_RepositorySaveFails_ThrowsDataAccessException() {
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenThrow(new DataIntegrityViolationException("Database constraint violation"));

        assertThrows(DataIntegrityViolationException.class, () ->
                appointmentService.createAppointment(testAppointment));
    }

    // updateAppointment Tests

    @Test
    void updateAppointment_SuccessfulUpdate_ReturnsUpdatedAppointment() {
        String appointmentId = "appointment123";
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.updateAppointment(appointmentId, testAppointment);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        assertNotNull(result.getUpdatedAt());
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void updateAppointment_ConflictWithOtherAppointment_ThrowsRuntimeException() {
        String appointmentId = "appointment123";
        Appointment conflictAppointment = createConflictAppointment("differentAppointment");
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(List.of(conflictAppointment));
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                appointmentService.updateAppointment(appointmentId, testAppointment));

        assertTrue(exception.getMessage().contains("Conflicting Appointment"));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_NoConflictWithSameAppointment_UpdatesSuccessfully() {
        String appointmentId = "appointment123";
        Appointment sameAppointment = createConflictAppointment(appointmentId);
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(List.of(sameAppointment));
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.updateAppointment(appointmentId, testAppointment);

        assertNotNull(result);
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void updateAppointment_RepositorySaveFails_ThrowsDataAccessException() {
        String appointmentId = "appointment123";
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenThrow(new DataAccessException("Database error") {});

        assertThrows(DataAccessException.class, () ->
                appointmentService.updateAppointment(appointmentId, testAppointment));
    }

    @Test
    void updateAppointment_EmptyConflictsList_UpdatesSuccessfully() {
        String appointmentId = "appointment123";
        when(appointmentRepository.findDoctorCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findPatientCollision(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.updateAppointment(appointmentId, testAppointment);

        assertNotNull(result);
        verify(appointmentRepository).save(testAppointment);
    }

    // getAppointmentById Tests

    @Test
    void getAppointmentById_AppointmentFound_ReturnsOptionalWithAppointment() {
        String appointmentId = "appointment123";
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(testAppointment));

        Optional<Appointment> result = appointmentService.getAppointmentById(appointmentId);

        assertTrue(result.isPresent());
        assertEquals("appointment123", result.get().getId());
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void getAppointmentById_AppointmentNotFound_ReturnsEmptyOptional() {
        String appointmentId = "nonexistent123";
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        Optional<Appointment> result = appointmentService.getAppointmentById(appointmentId);

        assertFalse(result.isPresent());
        verify(appointmentRepository).findById(appointmentId);
    }

    // deleteAppointment Tests

    @Test
    void deleteAppointment_SuccessfulDeletion_CallsRepositoryDelete() {
        String appointmentId = "appointment123";
        doNothing().when(appointmentRepository).deleteById(appointmentId);

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository).deleteById(appointmentId);
    }

    @Test
    void deleteAppointment_DeleteNonExistentAppointment_CallsRepositoryDelete() {
        String appointmentId = "nonexistent123";
        doNothing().when(appointmentRepository).deleteById(appointmentId);

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository).deleteById(appointmentId);
    }

    // deleteMultipleAppointments Tests

    @Test
    void deleteMultipleAppointments_SuccessfulBulkDeletion_CallsRepositoryDeleteAll() {
        List<String> appointmentIds = List.of("appointment1", "appointment2", "appointment3");
        doNothing().when(appointmentRepository).deleteAllById(appointmentIds);

        appointmentService.deleteMultipleAppointments(appointmentIds);

        verify(appointmentRepository).deleteAllById(appointmentIds);
    }

    @Test
    void deleteMultipleAppointments_DeleteWithSomeNonExistentIds_CallsRepositoryDeleteAll() {
        List<String> appointmentIds = List.of("appointment1", "nonexistent", "appointment3");
        doNothing().when(appointmentRepository).deleteAllById(appointmentIds);

        appointmentService.deleteMultipleAppointments(appointmentIds);

        verify(appointmentRepository).deleteAllById(appointmentIds);
    }

    @Test
    void deleteMultipleAppointments_DeleteWithEmptyList_CallsRepositoryDeleteAll() {
        List<String> appointmentIds = Collections.emptyList();
        doNothing().when(appointmentRepository).deleteAllById(appointmentIds);

        appointmentService.deleteMultipleAppointments(appointmentIds);

        verify(appointmentRepository).deleteAllById(appointmentIds);
    }

    // getAllAppointments Tests

    @Test
    void getAllAppointments_ReturnsAllAppointments_ReturnsFullList() {
        List<Appointment> appointments = List.of(testAppointment, createConflictAppointment("appointment2"));
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAllAppointments();

        assertEquals(2, result.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    void getAllAppointments_ReturnsEmptyList_ReturnsEmptyList() {
        when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAllAppointments();

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findAll();
    }

    // getAppointmentsByDoctor Tests

    @Test
    void getAppointmentsByDoctor_ReturnsDoctorAppointments_ReturnsListOfAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByDoctorId(doctorId)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDoctor(doctorId);

        assertEquals(1, result.size());
        assertEquals(doctorId, result.getFirst().getDoctorId());
        verify(appointmentRepository).findByDoctorId(doctorId);
    }

    @Test
    void getAppointmentsByDoctor_DoctorWithNoAppointments_ReturnsEmptyList() {
        when(appointmentRepository.findByDoctorId(doctorId)).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByDoctor(doctorId);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByDoctorId(doctorId);
    }

    @Test
    void getAppointmentsByDoctor_NonExistentDoctor_ReturnsEmptyList() {
        String nonExistentDoctorId = "nonexistent123";
        when(appointmentRepository.findByDoctorId(nonExistentDoctorId)).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByDoctor(nonExistentDoctorId);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByDoctorId(nonExistentDoctorId);
    }

    // getAppointmentsByPatient Tests

    @Test
    void getAppointmentsByPatient_ReturnsPatientAppointments_ReturnsListOfAppointments() {
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByPatient(patientId);

        assertEquals(1, result.size());
        assertEquals(patientId, result.getFirst().getPatientId());
        verify(appointmentRepository).findByPatientId(patientId);
    }

    @Test
    void getAppointmentsByPatient_PatientWithNoAppointments_ReturnsEmptyList() {
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByPatient(patientId);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByPatientId(patientId);
    }

    @Test
    void getAppointmentsByPatient_NonExistentPatient_ReturnsEmptyList() {
        String nonExistentPatientId = "nonexistent123";
        when(appointmentRepository.findByPatientId(nonExistentPatientId)).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByPatient(nonExistentPatientId);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByPatientId(nonExistentPatientId);
    }

    // getAppointmentsByDateRange Tests

    @Test
    void getAppointmentsByDateRange_ReturnsAppointmentsInDateRange_ReturnsListOfAppointments() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 12, 0);
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByStartTimeBetween(start, end)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDateRange(start, end);

        assertEquals(1, result.size());
        verify(appointmentRepository).findByStartTimeBetween(start, end);
    }

    @Test
    void getAppointmentsByDateRange_InvalidDateRange_ReturnsEmptyList() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 9, 0);
        when(appointmentRepository.findByStartTimeBetween(start, end)).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByDateRange(start, end);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByStartTimeBetween(start, end);
    }

    // getAppointmentsByDoctorAndDateRange Tests

    @Test
    void getAppointmentsByDoctorAndDateRange_ReturnsDoctorAppointmentsInDateRange_ReturnsListOfAppointments() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 12, 0);
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByDoctorIdAndStartTimeBetween(doctorId, start, end)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDoctorAndDateRange(doctorId, start, end);

        assertEquals(1, result.size());
        verify(appointmentRepository).findByDoctorIdAndStartTimeBetween(doctorId, start, end);
    }

    @Test
    void getAppointmentsByDoctorAndDateRange_NonExistentDoctor_ReturnsEmptyList() {
        String nonExistentDoctorId = "nonexistent123";
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 12, 0);
        when(appointmentRepository.findByDoctorIdAndStartTimeBetween(nonExistentDoctorId, start, end))
                .thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByDoctorAndDateRange(nonExistentDoctorId, start, end);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByDoctorIdAndStartTimeBetween(nonExistentDoctorId, start, end);
    }

    @Test
    void getAppointmentsByDoctorAndDateRange_InvalidDateRange_ReturnsEmptyList() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 9, 0);
        when(appointmentRepository.findByDoctorIdAndStartTimeBetween(doctorId, start, end))
                .thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByDoctorAndDateRange(doctorId, start, end);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByDoctorIdAndStartTimeBetween(doctorId, start, end);
    }

    // getAppointmentsByPatientAndDateRange Tests

    @Test
    void getAppointmentsByPatientAndDateRange_ReturnsPatientAppointmentsInDateRange_ReturnsListOfAppointments() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 12, 0);
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByPatientIdAndStartTimeBetween(patientId, start, end)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByPatientAndDateRange(patientId, start, end);

        assertEquals(1, result.size());
        verify(appointmentRepository).findByPatientIdAndStartTimeBetween(patientId, start, end);
    }

    @Test
    void getAppointmentsByPatientAndDateRange_NonExistentPatient_ReturnsEmptyList() {
        String nonExistentPatientId = "nonexistent123";
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 12, 0);
        when(appointmentRepository.findByPatientIdAndStartTimeBetween(nonExistentPatientId, start, end))
                .thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByPatientAndDateRange(nonExistentPatientId, start, end);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByPatientIdAndStartTimeBetween(nonExistentPatientId, start, end);
    }

    @Test
    void getAppointmentsByPatientAndDateRange_InvalidDateRange_ReturnsEmptyList() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 15, 9, 0);
        when(appointmentRepository.findByPatientIdAndStartTimeBetween(patientId, start, end))
                .thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByPatientAndDateRange(patientId, start, end);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByPatientIdAndStartTimeBetween(patientId, start, end);
    }

    // getAppointmentsByStatus Tests

    @Test
    void getAppointmentsByStatus_ReturnsAppointmentsWithGivenStatus_ReturnsListOfAppointments() {
        String status = "scheduled";
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByStatus(status)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByStatus(status);

        assertEquals(1, result.size());
        assertEquals(status, result.getFirst().getStatus());
        verify(appointmentRepository).findByStatus(status);
    }

    @Test
    void getAppointmentsByStatus_InvalidStatus_ReturnsEmptyList() {
        String invalidStatus = "invalidStatus";
        when(appointmentRepository.findByStatus(invalidStatus)).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByStatus(invalidStatus);

        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByStatus(invalidStatus);
    }
}