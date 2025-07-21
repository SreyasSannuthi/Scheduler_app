package com.scheduler.schedulerapp.service;

import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.repository.PatientRepository;
import com.scheduler.schedulerapp.service.patient.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient1;
    private Patient patient2;
    private Patient patient3;
    private Patient adminPatient;

    @BeforeEach
    void setUp() {
        patient1 = new Patient("1", "John Smith", "john.smith@email.com", "9876543210", 25, "patient", "patient123");
        patient2 = new Patient("2", "Jane Doe", "jane.doe@email.com", "9876543211", 30, "patient", "patient123");
        patient3 = new Patient("3", "Bob Wilson", "bob.wilson@email.com", "9876543212", 35, "patient", "patient123");
        adminPatient = new Patient("4", "Admin User", "admin@email.com", "9876543213", 40, "admin", "admin123");
    }

    @Test
    void getAllPatients_ShouldReturnAllPatients() {
        List<Patient> expectedPatients = Arrays.asList(patient1, patient2, patient3, adminPatient);
        when(patientRepository.findAll()).thenReturn(expectedPatients);
        List<Patient> result = patientService.getAllPatients();
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(expectedPatients, result);
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getAllPatients_WhenNoPatientsExist_ShouldReturnEmptyList() {
        when(patientRepository.findAll()).thenReturn(Arrays.asList());
        List<Patient> result = patientService.getAllPatients();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientById_WhenPatientExists_ShouldReturnPatient() {
        String patientId = "1";
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient1));
        Optional<Patient> result = patientService.getPatientById(patientId);
        assertTrue(result.isPresent());
        assertEquals(patient1, result.get());
        assertEquals("1", result.get().getId());
        assertEquals("John Smith", result.get().getName());
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    void getPatientById_WhenPatientDoesNotExist_ShouldReturnEmptyOptional() {
        String patientId = "999";
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());
        Optional<Patient> result = patientService.getPatientById(patientId);
        assertFalse(result.isPresent());
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    void getPatientById_WithNullId_ShouldReturnEmptyOptional() {
        when(patientRepository.findById(null)).thenReturn(Optional.empty());
        Optional<Patient> result = patientService.getPatientById(null);
        assertFalse(result.isPresent());
        verify(patientRepository, times(1)).findById(null);
    }

    @Test
    void getPatientsByRole_WhenPatientsExist_ShouldReturnPatientsWithRole() {
        String role = "patient";
        List<Patient> expectedPatients = Arrays.asList(patient1, patient2, patient3);
        when(patientRepository.findByRole(role)).thenReturn(expectedPatients);
        List<Patient> result = patientService.getPatientsByRole(role);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(patient -> role.equals(patient.getRole())));
        verify(patientRepository, times(1)).findByRole(role);
    }

    @Test
    void getPatientsByRole_WhenNoPatientsWithRole_ShouldReturnEmptyList() {
        String role = "nurse";
        when(patientRepository.findByRole(role)).thenReturn(Arrays.asList());
        List<Patient> result = patientService.getPatientsByRole(role);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findByRole(role);
    }

    @Test
    void getPatientsByRole_WithInvalidRole_ShouldReturnEmptyList() {
        String role = "invalidRole";
        when(patientRepository.findByRole(role)).thenReturn(Arrays.asList());
        List<Patient> result = patientService.getPatientsByRole(role);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findByRole(role);
    }

    @Test
    void getPatientsByRole_WithNullRole_ShouldReturnEmptyList() {
        when(patientRepository.findByRole(null)).thenReturn(Arrays.asList());
        List<Patient> result = patientService.getPatientsByRole(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findByRole(null);
    }

    @Test
    void createPatient_ShouldSaveAndReturnPatient() {
        Patient newPatient = new Patient(null, "New Patient", "new.patient@email.com", "9876543214", 28, "patient",
                "patient123");
        Patient savedPatient = new Patient("5", "New Patient", "new.patient@email.com", "9876543214", 28, "patient",
                "patient123");
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        Patient result = patientService.createPatient(newPatient);
        assertNotNull(result);
        assertEquals("5", result.getId());
        assertEquals("New Patient", result.getName());
        assertEquals("new.patient@email.com", result.getEmail());
        assertEquals("9876543214", result.getPhoneNumber());
        assertEquals(28, result.getAge());
        assertEquals("patient", result.getRole());
        verify(patientRepository, times(1)).save(newPatient);
    }

    @Test
    void createPatient_WithNullPatient_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            patientService.createPatient(null);
        });
        verify(patientRepository, never()).save(any());
    }

    @Test
    void updatePatient_ShouldSetIdAndSavePatient() {
        String patientId = "1";
        Patient updatedPatientInput = new Patient("999", "Updated Name", "updated@email.com", "9999999999", 35, "admin",
                "admin123");
        Patient savedPatient = new Patient("1", "Updated Name", "updated@email.com", "9999999999", 35, "admin",
                "admin123");
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        Patient result = patientService.updatePatient(patientId, updatedPatientInput);
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@email.com", result.getEmail());
        assertEquals("9999999999", result.getPhoneNumber());
        assertEquals(35, result.getAge());
        assertEquals("admin", result.getRole());
        verify(patientRepository, times(1)).save(updatedPatientInput);
        assertEquals(patientId, updatedPatientInput.getId());
    }

    @Test
    void updatePatient_WithDifferentOriginalId_ShouldOverwriteIdAndSave() {
        String patientId = "1";
        Patient patientWithDifferentId = new Patient("999", "Different Patient", "different@email.com", "8888888888",
                40, "patient", "patient123");
        Patient savedPatient = new Patient("1", "Different Patient", "different@email.com", "8888888888", 40, "patient",
                "patient123");
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        Patient result = patientService.updatePatient(patientId, patientWithDifferentId);
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Different Patient", result.getName());
        verify(patientRepository, times(1)).save(patientWithDifferentId);
        assertEquals(patientId, patientWithDifferentId.getId());
    }

    @Test
    void updatePatient_WithNullPatient_ShouldThrowException() {
        String patientId = "1";
        assertThrows(IllegalArgumentException.class, () -> {
            patientService.updatePatient(patientId, null);
        });
        verify(patientRepository, never()).save(any());
    }

    @Test
    void deletePatient_WithValidId_ShouldDeleteSuccessfully() {
        String patientId = "1";
        doNothing().when(patientRepository).deleteById(patientId);
        assertDoesNotThrow(() -> {
            patientService.deletePatient(patientId);
        });
        verify(patientRepository, times(1)).deleteById(patientId);
    }

    @Test
    void deletePatient_WithNonExistentId_ShouldCompleteWithoutError() {
        String patientId = "999";
        doNothing().when(patientRepository).deleteById(patientId);
        assertDoesNotThrow(() -> {
            patientService.deletePatient(patientId);
        });
        verify(patientRepository, times(1)).deleteById(patientId);
    }

    @Test
    void deletePatient_WithNullId_ShouldHandleGracefully() {
        doNothing().when(patientRepository).deleteById(null);
        assertDoesNotThrow(() -> {
            patientService.deletePatient(null);
        });
        verify(patientRepository, times(1)).deleteById(null);
    }
}