package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.AppointmentInputDTO;
import com.scheduler.schedulerapp.dto.AppointmentUpdateInputDTO;
import com.scheduler.schedulerapp.dto.AppointmentResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.model.User;
import com.scheduler.schedulerapp.service.appointment.AppointmentService;
import com.scheduler.schedulerapp.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AppointmentResolver {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public List<AppointmentResponseDTO> appointments(@Argument String userId) {
        Optional<User> user = userService.getUserById(userId);

        List<Appointment> appointments;
        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            appointments = appointmentService.getAllAppointments();
        } else {
            appointments = appointmentService.getAppointmentsByUser(userId);
        }

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByUser(@Argument String userId) {
        return appointments(userId);
    }

    @QueryMapping
    public AppointmentResponseDTO appointmentById(@Argument String id, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);

        if (appointment.isPresent() && user.isPresent() && !"admin".equals(user.get().getRole()) &&
                !userId.equals(appointment.get().getUserId())) {
            return null;
        }

        return appointment.map(dtoMapper::toAppointmentResponseDTO).orElse(null);
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByDateRange(@Argument String startDate, @Argument String endDate, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);
        LocalDateTime start = parseDateTime(startDate);
        LocalDateTime end = parseDateTime(endDate);

        List<Appointment> appointments;
        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            appointments = appointmentService.getAppointmentsByDateRange(start, end);
        } else {
            appointments = appointmentService.getAppointmentsByUserAndDateRange(userId, start, end);
        }

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByUserAndDateRange(@Argument String userId, @Argument String startDate, @Argument String endDate) {
        return appointmentsByDateRange(startDate, endDate, userId);
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByCategory(@Argument String category, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);

        List<Appointment> appointments;
        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            appointments = appointmentService.getAppointmentsByCategory(category);
        } else {
            appointments = appointmentService.getAppointmentsByUser(userId)
                    .stream()
                    .filter(apt -> category.equals(apt.getCategory()))
                    .toList();
        }

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> appointmentsByStatus(@Argument String status, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);

        List<Appointment> appointments;
        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            appointments = appointmentService.getAppointmentsByStatus(status);
        } else {
            appointments = appointmentService.getAppointmentsByUser(userId)
                    .stream()
                    .filter(apt -> status.equals(apt.getStatus()))
                    .toList();
        }

        return appointments.stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AppointmentResponseDTO> checkCollision(@Argument String userId, @Argument String startTime, @Argument String endTime) {
        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);

        return appointmentService.checkCollision(userId, start, end).stream()
                .map(dtoMapper::toAppointmentResponseDTO)
                .collect(Collectors.toList());
    }

    @MutationMapping
    public AppointmentResponseDTO createAppointment(@Valid @Argument AppointmentInputDTO input, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);

        if (user.isEmpty() || !"admin".equals(user.get().getRole())) {
            input.setUserId(userId);
        }

        LocalDateTime startTime = parseDateTime(input.getStartTime());
        LocalDateTime endTime = parseDateTime(input.getEndTime());

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create appointments in the past");
        }

        Appointment appointment = new Appointment();
        appointment.setTitle(input.getTitle());
        appointment.setDescription(input.getDescription());
        appointment.setUserId(input.getUserId());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(input.getStatus() != null ? input.getStatus() : "scheduled");
        appointment.setCategory(input.getCategory() != null ? input.getCategory() : "work");

        Appointment savedAppointment = appointmentService.createAppointment(appointment);
        return dtoMapper.toAppointmentResponseDTO(savedAppointment);
    }

    @MutationMapping
    public AppointmentResponseDTO updateAppointment(@Argument String id, @Valid @Argument AppointmentUpdateInputDTO input, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Appointment> existing = appointmentService.getAppointmentById(id);

        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }

        Appointment appointment = existing.get();

        if (user.isEmpty() || (!"admin".equals(user.get().getRole()) && !userId.equals(appointment.getUserId()))) {
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
        if (input.getCategory() != null) {
            appointment.setCategory(input.getCategory());
        }

        Appointment updatedAppointment = appointmentService.updateAppointment(id, appointment);
        return dtoMapper.toAppointmentResponseDTO(updatedAppointment);
    }

    @MutationMapping
    public Boolean deleteAppointment(@Argument String id, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);

        if (appointment.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }

        if (user.isEmpty() || (!"admin".equals(user.get().getRole()) && !userId.equals(appointment.get().getUserId()))) {
            throw new SecurityException("Access denied: You can only delete your own appointments");
        }

        appointmentService.deleteAppointment(id);
        return true;
    }

    @MutationMapping
    public Boolean deleteMultipleAppointments(@Argument List<String> ids, @Argument String userId) {
        Optional<User> user = userService.getUserById(userId);

        for (String id : ids) {
            Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
            if (appointment.isEmpty()) {
                throw new IllegalArgumentException("Appointment not found with ID: " + id);
            }

            if (user.isEmpty() || (!"admin".equals(user.get().getRole()) && !userId.equals(appointment.get().getUserId()))) {
                throw new SecurityException("Access denied: You can only delete your own appointments");
            }
        }

        appointmentService.deleteMultipleAppointments(ids);
        return true;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use ISO format: 2024-12-15T10:00:00");
        }
    }
}