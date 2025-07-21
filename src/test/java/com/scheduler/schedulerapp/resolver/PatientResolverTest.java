package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.PatientResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.service.patient.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(roles = "ADMIN")
class PatientResolverTest {

    @Mock
    private PatientService patientService;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private PatientResolver patientResolver;

    private Patient patient1;
    private Patient patient2;
    private Patient adminPatient;
    private PatientResponseDTO patientResponseDTO1;
    private PatientResponseDTO patientResponseDTO2;
    private PatientResponseDTO adminPatientResponseDTO;

    @BeforeEach
    void setUp() {
        patient1 = new Patient("1", "John Smith", "john.smith@email.com", "9876543210", 25, "patient", "patient123");
        patient2 = new Patient("2", "Jane Doe", "jane.doe@email.com", "9876543211", 30, "patient","patient123");
        adminPatient = new Patient("3", "Admin User", "admin@email.com", "9876543212", 40, "admin", "admin123");

        patientResponseDTO1 = new PatientResponseDTO("1", "John Smith", "john.smith@email.com", "9876543210", 25, "patient");
        patientResponseDTO2 = new PatientResponseDTO("2", "Jane Doe", "jane.doe@email.com", "9876543211", 30, "patient");
        adminPatientResponseDTO = new PatientResponseDTO("3", "Admin User", "admin@email.com", "9876543212", 40, "admin");
    }

    @Test
    void patients_WhenMultiplePatientsExist_ShouldReturnListOfPatientResponseDTO() {
        List<Patient> patients = Arrays.asList(patient1, patient2, adminPatient);
        List<PatientResponseDTO> expectedDTOs = Arrays.asList(patientResponseDTO1, patientResponseDTO2, adminPatientResponseDTO);
        when(patientService.getAllPatients()).thenReturn(patients);
        when(dtoMapper.toPatientResponseDTO(patient1)).thenReturn(patientResponseDTO1);
        when(dtoMapper.toPatientResponseDTO(patient2)).thenReturn(patientResponseDTO2);
        when(dtoMapper.toPatientResponseDTO(adminPatient)).thenReturn(adminPatientResponseDTO);
        List<PatientResponseDTO> result = patientResolver.patients();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedDTOs, result);
        verify(patientService, times(1)).getAllPatients();
        verify(dtoMapper, times(1)).toPatientResponseDTO(patient1);
        verify(dtoMapper, times(1)).toPatientResponseDTO(patient2);
        verify(dtoMapper, times(1)).toPatientResponseDTO(adminPatient);
    }

    @Test
    void patients_WhenEmptyDatabase_ShouldReturnEmptyList() {
        when(patientService.getAllPatients()).thenReturn(Arrays.asList());
        List<PatientResponseDTO> result = patientResolver.patients();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientService, times(1)).getAllPatients();
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patients_WhenServiceThrowsException_ShouldPropagateException() {
        when(patientService.getAllPatients()).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> {
            patientResolver.patients();
        });
        verify(patientService, times(1)).getAllPatients();
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patient_WhenValidExistingId_ShouldReturnPatientResponseDTO() {
        String patientId = "1";
        when(patientService.getPatientById(patientId)).thenReturn(Optional.of(patient1));
        when(dtoMapper.toPatientResponseDTO(patient1)).thenReturn(patientResponseDTO1);
        PatientResponseDTO result = patientResolver.patient(patientId);
        assertNotNull(result);
        assertEquals(patientResponseDTO1, result);
        assertEquals("1", result.getId());
        assertEquals("John Smith", result.getName());
        assertEquals("9876543210", result.getPhoneNumber());
        assertEquals(25, result.getAge());
        verify(patientService, times(1)).getPatientById(patientId);
        verify(dtoMapper, times(1)).toPatientResponseDTO(patient1);
    }

    @Test
    void patient_WhenInvalidId_ShouldReturnNull() {
        String patientId = "999";
        when(patientService.getPatientById(patientId)).thenReturn(Optional.empty());
        PatientResponseDTO result = patientResolver.patient(patientId);
        assertNull(result);
        verify(patientService, times(1)).getPatientById(patientId);
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patient_WithNullId_ShouldReturnNull() {
        when(patientService.getPatientById(null)).thenReturn(Optional.empty());
        PatientResponseDTO result = patientResolver.patient(null);
        assertNull(result);
        verify(patientService, times(1)).getPatientById(null);
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patient_WhenServiceThrowsException_ShouldPropagateException() {
        String patientId = "1";
        when(patientService.getPatientById(patientId)).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> {
            patientResolver.patient(patientId);
        });
        verify(patientService, times(1)).getPatientById(patientId);
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patientsByRole_WhenValidRoleWithExistingPatients_ShouldReturnFilteredList() {
        String role = "patient";
        List<Patient> patients = Arrays.asList(patient1, patient2);
        List<PatientResponseDTO> expectedDTOs = Arrays.asList(patientResponseDTO1, patientResponseDTO2);
        when(patientService.getPatientsByRole(role)).thenReturn(patients);
        when(dtoMapper.toPatientResponseDTO(patient1)).thenReturn(patientResponseDTO1);
        when(dtoMapper.toPatientResponseDTO(patient2)).thenReturn(patientResponseDTO2);
        List<PatientResponseDTO> result = patientResolver.patientsByRole(role);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDTOs, result);
        verify(patientService, times(1)).getPatientsByRole(role);
        verify(dtoMapper, times(1)).toPatientResponseDTO(patient1);
        verify(dtoMapper, times(1)).toPatientResponseDTO(patient2);
    }

    @Test
    void patientsByRole_WhenValidRoleWithNoMatchingPatients_ShouldReturnEmptyList() {
        String role = "nurse";
        when(patientService.getPatientsByRole(role)).thenReturn(Arrays.asList());
        List<PatientResponseDTO> result = patientResolver.patientsByRole(role);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientService, times(1)).getPatientsByRole(role);
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patientsByRole_WithInvalidRole_ShouldReturnEmptyList() {
        String role = "invalidRole";
        when(patientService.getPatientsByRole(role)).thenReturn(Arrays.asList());
        List<PatientResponseDTO> result = patientResolver.patientsByRole(role);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientService, times(1)).getPatientsByRole(role);
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patientsByRole_WithNullRole_ShouldReturnEmptyList() {
        when(patientService.getPatientsByRole(null)).thenReturn(Arrays.asList());
        List<PatientResponseDTO> result = patientResolver.patientsByRole(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientService, times(1)).getPatientsByRole(null);
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }

    @Test
    void patientsByRole_WhenServiceThrowsException_ShouldPropagateException() {
        String role = "patient";
        when(patientService.getPatientsByRole(role)).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> {
            patientResolver.patientsByRole(role);
        });
        verify(patientService, times(1)).getPatientsByRole(role);
        verify(dtoMapper, never()).toPatientResponseDTO(any());
    }
}