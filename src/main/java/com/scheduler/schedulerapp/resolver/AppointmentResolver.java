package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.AppointmentInputDTO;
import com.scheduler.schedulerapp.dto.AppointmentUpdateInputDTO;
import com.scheduler.schedulerapp.dto.AppointmentResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.StaffBranchMapping;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.service.appointment.AppointmentService;
import com.scheduler.schedulerapp.service.branchmapping.DoctorBranchMappingService;
import com.scheduler.schedulerapp.service.doctor.DoctorService;
import com.scheduler.schedulerapp.service.patient.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AppointmentResolver {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DTOMapper dtoMapper;

    @Autowired
    private DoctorBranchMappingService doctorBranchMappingService;

    private static final Set<String> ADMIN_IDS = Set.of(
            "6887727cf3498c1806036f28");

    private boolean hasFullAppointmentAccess(String userId) {
        Optional<HospitalStaff> user = doctorService.getDoctorById(userId);
        if (user.isPresent()) {
            return user.get().hasFullAppointmentAccess(); // admin || customer_care
        }
        return false;
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointments(@Argument String requesterId) {
        Optional<HospitalStaff> user = doctorService.getDoctorById(requesterId);
        if (user.isEmpty()) {
            throw new SecurityException("User not found");
        }

        HospitalStaff doctor = user.get();
        List<Appointment> appointments;

        if (doctor.hasFullAppointmentAccess()) {
            appointments = appointmentService.getAllAppointments();
        } else if (doctor.isReceptionist()) {
            List<StaffBranchMapping> branchMappings = doctorBranchMappingService.getDoctorBranches(requesterId);
            appointments = new ArrayList<>();
            for (StaffBranchMapping mapping : branchMappings) {
                appointments.addAll(appointmentService.getAppointmentsByBranch(mapping.getBranchId()));
            }
        } else {
            throw new SecurityException("Access denied");
        }

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByDateRange(@Argument String requesterId, @Argument String startDate,
                                                                @Argument String endDate) {
        LocalDateTime start = parseDateTime(startDate);
        LocalDateTime end = parseDateTime(endDate);

        if (!hasFullAppointmentAccess(requesterId)) {
            throw new SecurityException("Access denied: Only admin and customer care can view all appointments");
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(start, end);
        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByStatus(@Argument String status, @Argument String requesterId) {
        List<Appointment> appointments;

        if (hasFullAppointmentAccess(requesterId)) {
            appointments = appointmentService.getAppointmentsByStatus(status);
        } else {
            appointments = appointmentService.getAppointmentsByDoctor(requesterId)
                    .stream()
                    .filter(apt -> status.equals(apt.getStatus()))
                    .toList();
        }

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByDoctor(@Argument String doctorId) {
        Optional<HospitalStaff> doctor = doctorService.getDoctorById(doctorId);

        if (doctor.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByPatient(@Argument String patientId) {
        Optional<Patient> patient = patientService.getPatientById(patientId);

        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public AppointmentResponseDTO appointmentById(@Argument String id, @Argument String requesterId) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);

        if (appointment.isEmpty()) {
            return null;
        }

        if (!ADMIN_IDS.contains(requesterId) && !requesterId.equals(appointment.get().getDoctorId()) &&
                !requesterId.equals(appointment.get().getPatientId())) {
            return null;
        }

        return dtoMapper.toAppointmentResponseDTO(appointment.get());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByDoctorAndDateRange(@Argument String doctorId,
            @Argument String startDate, @Argument String endDate) {
        Optional<HospitalStaff> doctor = doctorService.getDoctorById(doctorId);
        LocalDateTime start = parseDateTime(startDate);
        LocalDateTime end = parseDateTime(endDate);

        if (doctor.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorAndDateRange(doctorId, start, end);

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByPatientAndDateRange(@Argument String patientId,
            @Argument String startDate, @Argument String endDate) {
        Optional<Patient> patient = patientService.getPatientById(patientId);
        LocalDateTime start = parseDateTime(startDate);
        LocalDateTime end = parseDateTime(endDate);

        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByPatientAndDateRange(patientId, start, end);

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> checkCollision(@Argument String doctorId, @Argument String patientId,
            @Argument String startTime, @Argument String endTime) {

        Optional<HospitalStaff> doctor = doctorService.getDoctorById(doctorId);
        Optional<Patient> patient = patientService.getPatientById(patientId);

        if (doctor.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }

        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);

        return appointmentService.checkCollision(doctorId, patientId, start, end).stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @MutationMapping
    public AppointmentResponseDTO createAppointment(@Valid @Argument AppointmentInputDTO input) {
        Optional<HospitalStaff> doctor = doctorService.getDoctorById(input.getDoctorId());
        if (doctor.isEmpty()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + input.getDoctorId());
        }

        Optional<Patient> patient = patientService.getPatientById(input.getPatientId());
        if (patient.isEmpty()) {
            throw new IllegalArgumentException("Patient not found with ID: " + input.getPatientId());
        }

        LocalDateTime startTime = parseDateTime(input.getStartTime());
        LocalDateTime endTime = parseDateTime(input.getEndTime());

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create appointments in the past");
        }

        Appointment appointment = new Appointment();
        appointment.setTitle(input.getTitle());
        appointment.setDescription(input.getDescription());
        appointment.setDoctorId(input.getDoctorId());
        appointment.setPatientId(input.getPatientId());
        appointment.setBranchId(input.getBranchId());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(input.getStatus() != null ? input.getStatus() : "scheduled");

        Appointment savedAppointment = appointmentService.createAppointment(appointment);
        return dtoMapper.toAppointmentResponseDTO(savedAppointment);
    }

    @MutationMapping
    public AppointmentResponseDTO updateAppointment(@Argument String id,
            @Valid @Argument AppointmentUpdateInputDTO input, @Argument String requesterId) {

        Optional<Appointment> existing = appointmentService.getAppointmentById(id);

        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }

        Appointment appointment = existing.get();

        if (!ADMIN_IDS.contains(requesterId) && !requesterId.equals(appointment.getDoctorId())
                && !requesterId.equals(appointment.getPatientId())) {
            throw new SecurityException("Access denied: You can only update your own appointments");
        }

        if (input.getTitle() != null) {
            appointment.setTitle(input.getTitle());
        }
        if (input.getDescription() != null) {
            appointment.setDescription(input.getDescription());
        }
        if (input.getStartTime() != null) {
            LocalDateTime newStartTime = parseDateTime(input.getStartTime());
            if (newStartTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Cannot update appointment to past time");
            }
            appointment.setStartTime(newStartTime);
        }
        if (input.getEndTime() != null) {
            appointment.setEndTime(parseDateTime(input.getEndTime()));
        }
        if (input.getStatus() != null) {
            appointment.setStatus(input.getStatus());
        }
        if (input.getBranchId() != null) {
            appointment.setBranchId(input.getBranchId());
        }

        Appointment updatedAppointment = appointmentService.updateAppointment(id, appointment);
        return dtoMapper.toAppointmentResponseDTO(updatedAppointment);
    }

    @MutationMapping
    public Boolean deleteAppointment(@Argument String id, @Argument String requesterId) {

        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);

        if (appointment.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }

        if (!ADMIN_IDS.contains(requesterId) && !requesterId.equals(appointment.get().getDoctorId())
                && !requesterId.equals(appointment.get().getPatientId())) {
            throw new SecurityException("Access denied: You can only delete your own appointments");
        }

        appointmentService.deleteAppointment(id);
        return true;
    }

    @MutationMapping
    public Boolean deleteMultipleAppointments(@Argument List<String> ids, @Argument String requesterId) {
        for (String id : ids) {
            Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
            if (appointment.isEmpty()) {
                throw new IllegalArgumentException("Appointment not found with ID: " + id);
            }

            if (!ADMIN_IDS.contains(requesterId) && !requesterId.equals(appointment.get().getDoctorId())
                    && !requesterId.equals(appointment.get().getPatientId())) {
                throw new SecurityException("Access denied: You can only delete your own appointments");
            }
        }

        appointmentService.deleteMultipleAppointments(ids);
        return true;
    }


    private boolean hasBranchAccess(String userId, String branchId) {
        Optional<HospitalStaff> user = doctorService.getDoctorById(userId);
        if (user.isPresent()) {
            HospitalStaff doctor = user.get();
            if (doctor.isAdmin() || doctor.isCustomerCare()) {
                return true;
            }
            if (doctor.isReceptionist()) {
                return doctorBranchMappingService.isDoctorAssignedToBranch(userId, branchId);
            }
        }
        return false;
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByBranch(@Argument String branchId, @Argument String requesterId) {
        if (!hasBranchAccess(requesterId, branchId)) {
            throw new SecurityException("Access denied: You don't have access to this branch");
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByBranch(branchId);
        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use ISO format: 2024-12-15T10:00:00");
        }
    }
}