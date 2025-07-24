package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.DoctorResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.Doctor;
import com.scheduler.schedulerapp.service.doctor.DoctorService;
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
class DoctorResolverTest {

    @Mock
    private DoctorService doctorService;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private DoctorResolver doctorResolver;

    private Doctor doctor1;
    private Doctor doctor2;
    private Doctor adminDoctor;
    private DoctorResponseDTO doctorResponseDTO1;
    private DoctorResponseDTO doctorResponseDTO2;
    private DoctorResponseDTO adminDoctorResponseDTO;

    @BeforeEach
    void setUp() {
        doctor1 = new Doctor("1", "Dr. John Smith", "john.smith@hospital.com", "doctor", "password123",
                "July 22 2025 5:51 PM", "", true);
        doctor2 = new Doctor("2", "Dr. Jane Doe", "jane.doe@hospital.com", "doctor", "password123",
                "July 22 2025 5:51 PM", "", true);
        adminDoctor = new Doctor("3", "Dr. Admin", "admin@hospital.com", "admin", "admin123",
                "July 22 2025 5:51 PM", "", true);

        doctorResponseDTO1 = new DoctorResponseDTO("1", "Dr. John Smith", "john.smith@hospital.com", "doctor",
                "July 22 2025 5:51 PM", "", true);
        doctorResponseDTO2 = new DoctorResponseDTO("2", "Dr. Jane Doe", "jane.doe@hospital.com", "doctor",
                "July 22 2025 5:51 PM", "", true);
        adminDoctorResponseDTO = new DoctorResponseDTO("3", "Dr. Admin", "admin@hospital.com", "admin",
                "July 22 2025 5:51 PM", "", true);
    }

    @Test
    void doctors_WhenMultipleDoctorsExist_ShouldReturnListOfDoctorResponseDTO() {
        List<Doctor> doctors = Arrays.asList(doctor1, doctor2, adminDoctor);
        List<DoctorResponseDTO> expectedDTOs = Arrays.asList(doctorResponseDTO1, doctorResponseDTO2,
                adminDoctorResponseDTO);
        when(doctorService.getAllDoctors()).thenReturn(doctors);
        when(dtoMapper.toDoctorResponseDTO(doctor1)).thenReturn(doctorResponseDTO1);
        when(dtoMapper.toDoctorResponseDTO(doctor2)).thenReturn(doctorResponseDTO2);
        when(dtoMapper.toDoctorResponseDTO(adminDoctor)).thenReturn(adminDoctorResponseDTO);
        List<DoctorResponseDTO> result = doctorResolver.doctors();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedDTOs, result);
        verify(doctorService, times(1)).getAllDoctors();
        verify(dtoMapper, times(1)).toDoctorResponseDTO(doctor1);
        verify(dtoMapper, times(1)).toDoctorResponseDTO(doctor2);
        verify(dtoMapper, times(1)).toDoctorResponseDTO(adminDoctor);
    }

    @Test
    void doctors_WhenEmptyDatabase_ShouldReturnEmptyList() {
        when(doctorService.getAllDoctors()).thenReturn(Arrays.asList());
        List<DoctorResponseDTO> result = doctorResolver.doctors();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(doctorService, times(1)).getAllDoctors();
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctors_WhenServiceThrowsException_ShouldPropagateException() {
        when(doctorService.getAllDoctors()).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> {
            doctorResolver.doctors();
        });
        verify(doctorService, times(1)).getAllDoctors();
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctor_WhenValidExistingId_ShouldReturnDoctorResponseDTO() {
        String doctorId = "1";
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.of(doctor1));
        when(dtoMapper.toDoctorResponseDTO(doctor1)).thenReturn(doctorResponseDTO1);
        DoctorResponseDTO result = doctorResolver.doctor(doctorId);
        assertNotNull(result);
        assertEquals(doctorResponseDTO1, result);
        assertEquals("1", result.getId());
        assertEquals("Dr. John Smith", result.getName());
        verify(doctorService, times(1)).getDoctorById(doctorId);
        verify(dtoMapper, times(1)).toDoctorResponseDTO(doctor1);
    }

    @Test
    void doctor_WhenInvalidId_ShouldReturnNull() {
        String doctorId = "999";
        when(doctorService.getDoctorById(doctorId)).thenReturn(Optional.empty());
        DoctorResponseDTO result = doctorResolver.doctor(doctorId);
        assertNull(result);
        verify(doctorService, times(1)).getDoctorById(doctorId);
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctor_WithNullId_ShouldReturnNull() {
        when(doctorService.getDoctorById(null)).thenReturn(Optional.empty());
        DoctorResponseDTO result = doctorResolver.doctor(null);
        assertNull(result);
        verify(doctorService, times(1)).getDoctorById(null);
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctor_WhenServiceThrowsException_ShouldPropagateException() {
        String doctorId = "1";
        when(doctorService.getDoctorById(doctorId)).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> {
            doctorResolver.doctor(doctorId);
        });
        verify(doctorService, times(1)).getDoctorById(doctorId);
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctorsByRole_WhenValidRoleWithExistingDoctors_ShouldReturnFilteredList() {
        String role = "doctor";
        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);
        List<DoctorResponseDTO> expectedDTOs = Arrays.asList(doctorResponseDTO1, doctorResponseDTO2);
        when(doctorService.getDoctorsByRole(role)).thenReturn(doctors);
        when(dtoMapper.toDoctorResponseDTO(doctor1)).thenReturn(doctorResponseDTO1);
        when(dtoMapper.toDoctorResponseDTO(doctor2)).thenReturn(doctorResponseDTO2);
        List<DoctorResponseDTO> result = doctorResolver.doctorsByRole(role);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDTOs, result);
        verify(doctorService, times(1)).getDoctorsByRole(role);
        verify(dtoMapper, times(1)).toDoctorResponseDTO(doctor1);
        verify(dtoMapper, times(1)).toDoctorResponseDTO(doctor2);
    }

    @Test
    void doctorsByRole_WhenValidRoleWithNoMatchingDoctors_ShouldReturnEmptyList() {
        String role = "nurse";
        when(doctorService.getDoctorsByRole(role)).thenReturn(Arrays.asList());
        List<DoctorResponseDTO> result = doctorResolver.doctorsByRole(role);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(doctorService, times(1)).getDoctorsByRole(role);
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctorsByRole_WithInvalidRole_ShouldReturnEmptyList() {
        String role = "invalidRole";
        when(doctorService.getDoctorsByRole(role)).thenReturn(Arrays.asList());
        List<DoctorResponseDTO> result = doctorResolver.doctorsByRole(role);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(doctorService, times(1)).getDoctorsByRole(role);
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctorsByRole_WithNullRole_ShouldReturnEmptyList() {
        when(doctorService.getDoctorsByRole(null)).thenReturn(Arrays.asList());
        List<DoctorResponseDTO> result = doctorResolver.doctorsByRole(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(doctorService, times(1)).getDoctorsByRole(null);
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }

    @Test
    void doctorsByRole_WhenServiceThrowsException_ShouldPropagateException() {
        String role = "doctor";
        when(doctorService.getDoctorsByRole(role)).thenThrow(new RuntimeException("Database error"));
        assertThrows(RuntimeException.class, () -> {
            doctorResolver.doctorsByRole(role);
        });
        verify(doctorService, times(1)).getDoctorsByRole(role);
        verify(dtoMapper, never()).toDoctorResponseDTO(any());
    }
}