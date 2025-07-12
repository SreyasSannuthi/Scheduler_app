package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.AppointmentInput;
import com.scheduler.schedulerapp.dto.AppointmentUpdateInput;
import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.model.User;
import com.scheduler.schedulerapp.repository.UserRepository;
import com.scheduler.schedulerapp.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
public class AppointmentResolver {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @QueryMapping
    public List<Appointment> appointments(@Argument String userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            return appointmentService.getAllAppointments();
        } else {
            return appointmentService.getAppointmentsByUser(userId);
        }
    }

    @QueryMapping
    public List<Appointment> appointmentsByUser(@Argument String userId) {
        return appointments(userId);
    }

    @QueryMapping
    public Optional<Appointment> appointmentById(@Argument String id, @Argument String userId) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID is required");
        }

        Optional<User> user = userRepository.findById(userId);
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);

        if (appointment.isPresent() && user.isPresent() && !"admin".equals(user.get().getRole()) &&
                !userId.equals(appointment.get().getUserId())) {
            return Optional.empty();
        }

        return appointment;
    }

    @QueryMapping
    public List<Appointment> appointmentsByDateRange(@Argument String startDate, @Argument String endDate, @Argument String userId) {
        Optional<User> user = userRepository.findById(userId);
        LocalDateTime start = validateAndParseDateTime(startDate, "Start date");
        LocalDateTime end = validateAndParseDateTime(endDate, "End date");

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            return appointmentService.getAppointmentsByDateRange(start, end);
        } else {
            return appointmentService.getAppointmentsByUserAndDateRange(userId, start, end);
        }
    }

    @QueryMapping
    public List<Appointment> appointmentsByUserAndDateRange(@Argument String userId, @Argument String startDate, @Argument String endDate) {
        return appointmentsByDateRange(startDate, endDate, userId);
    }

    @QueryMapping
    public List<Appointment> appointmentsByCategory(@Argument String category, @Argument String userId) {
        Optional<User> user = userRepository.findById(userId);
        validateCategory(category);

        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            return appointmentService.getAppointmentsByCategory(category);
        } else {
            return appointmentService.getAppointmentsByUser(userId)
                    .stream()
                    .filter(apt -> category.equals(apt.getCategory()))
                    .toList();
        }
    }

    @QueryMapping
    public List<Appointment> appointmentsByStatus(@Argument String status, @Argument String userId) {
        Optional<User> user = userRepository.findById(userId);
        validateStatus(status);

        if (user.isPresent() && "admin".equals(user.get().getRole())) {
            return appointmentService.getAppointmentsByStatus(status);
        } else {
            return appointmentService.getAppointmentsByUser(userId)
                    .stream()
                    .filter(apt -> status.equals(apt.getStatus()))
                    .toList();
        }
    }

    @QueryMapping
    public List<Appointment> checkCollision(@Argument String userId, @Argument String startTime, @Argument String endTime) {
        LocalDateTime start = validateAndParseDateTime(startTime, "Start time");
        LocalDateTime end = validateAndParseDateTime(endTime, "End time");

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        return appointmentService.checkCollision(userId, start, end);
    }

    @MutationMapping
    public Appointment createAppointment(@Argument AppointmentInput input, @Argument String userId) {
        Optional<User> user = userRepository.findById(userId);

        validateRequiredFields(input);

        if (user.isEmpty() || !"admin".equals(user.get().getRole())) {
            input.setUserId(userId);
        }

        LocalDateTime startTime = validateAndParseDateTime(input.getStartTime(), "Start time");
        LocalDateTime endTime = validateAndParseDateTime(input.getEndTime(), "End time");

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Duration duration = Duration.between(startTime, endTime);
        if (duration.toHours() > 4) {
            throw new IllegalArgumentException("Appointment duration cannot exceed 4 hours");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create appointments in the past");
        }

        if (input.getCategory() != null) {
            validateCategory(input.getCategory());
        }
        if (input.getStatus() != null) {
            validateStatus(input.getStatus());
        }

        Appointment appointment = new Appointment();
        appointment.setTitle(input.getTitle().trim());
        appointment.setDescription(input.getDescription() != null ? input.getDescription().trim() : null);
        appointment.setUserId(input.getUserId());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(input.getStatus() != null ? input.getStatus() : "scheduled");
        appointment.setCategory(input.getCategory() != null ? input.getCategory() : "work");

        return appointmentService.createAppointment(appointment);
    }

    @MutationMapping
    public Appointment updateAppointment(@Argument String id, @Argument AppointmentUpdateInput input, @Argument String userId) {
        Optional<User> user = userRepository.findById(userId);

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID is required");
        }

        Optional<Appointment> existing = appointmentService.getAppointmentById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }

        Appointment appointment = existing.get();

        if (user.isEmpty() || (!"admin".equals(user.get().getRole()) && !userId.equals(appointment.getUserId()))) {
            throw new SecurityException("Access denied: You can only update your own appointments");
        }

        if (input.getTitle() != null) {
            if (input.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            appointment.setTitle(input.getTitle().trim());
        }

        if (input.getDescription() != null) {
            appointment.setDescription(input.getDescription().trim());
        }

        LocalDateTime newStartTime = appointment.getStartTime();
        LocalDateTime newEndTime = appointment.getEndTime();

        if (input.getStartTime() != null) {
            newStartTime = validateAndParseDateTime(input.getStartTime(), "Start time");
        }
        if (input.getEndTime() != null) {
            newEndTime = validateAndParseDateTime(input.getEndTime(), "End time");
        }

        if (input.getStartTime() != null || input.getEndTime() != null) {
            if (!newEndTime.isAfter(newStartTime)) {
                throw new IllegalArgumentException("End time must be after start time");
            }

            Duration duration = Duration.between(newStartTime, newEndTime);
            if (duration.toHours() > 4) {
                throw new IllegalArgumentException("Appointment duration cannot exceed 4 hours");
            }

            if (newStartTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Cannot update appointment to past time");
            }

            appointment.setStartTime(newStartTime);
            appointment.setEndTime(newEndTime);
        }

        if (input.getStatus() != null) {
            validateStatus(input.getStatus());
            appointment.setStatus(input.getStatus());
        }
        if (input.getCategory() != null) {
            validateCategory(input.getCategory());
            appointment.setCategory(input.getCategory());
        }

        return appointmentService.updateAppointment(id, appointment);
    }

    @MutationMapping
    public Boolean deleteAppointment(@Argument String id, @Argument String userId) {
        Optional<User> user = userRepository.findById(userId);

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID is required");
        }

        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        if (appointment.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found with ID: " + id);
        }

        if (user.isEmpty() || (!"admin".equals(user.get().getRole()) && !userId.equals(appointment.get().getUserId()))) {
            throw new SecurityException("Access denied: You can only delete your own appointments");
        }

        try {
            appointmentService.deleteAppointment(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete appointment: " + e.getMessage());
        }
    }

    @MutationMapping
    public Boolean deleteMultipleAppointments(@Argument List<String> ids, @Argument String userId) {
        Optional<User> user = userRepository.findById(userId);

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("At least one appointment ID is required");
        }

        for (String id : ids) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid appointment ID in list");
            }

            Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
            if (appointment.isEmpty()) {
                throw new IllegalArgumentException("Appointment not found with ID: " + id);
            }

            if (user.isEmpty() || (!"admin".equals(user.get().getRole()) && !userId.equals(appointment.get().getUserId()))) {
                throw new SecurityException("Access denied: You can only delete your own appointments");
            }
        }

        try {
            appointmentService.deleteMultipleAppointments(ids);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete appointments: " + e.getMessage());
        }
    }

    private void validateRequiredFields(AppointmentInput input) {
        if (input.getTitle() == null || input.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required and cannot be empty");
        }

        if (input.getUserId() == null || input.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (input.getStartTime() == null || input.getStartTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Start time is required");
        }

        if (input.getEndTime() == null || input.getEndTime().trim().isEmpty()) {
            throw new IllegalArgumentException("End time is required");
        }
    }

    private LocalDateTime validateAndParseDateTime(String dateTimeStr, String fieldName) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            return LocalDateTime.parse(dateTimeStr.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " must be in ISO format (e.g., 2024-12-15T10:00:00)");
        }
    }

    private void validateCategory(String category) {
        if (category != null &&
                !List.of("work", "personal", "medical", "education", "social").contains(category)) {
            throw new IllegalArgumentException("Invalid category. Must be: work, personal, medical, education, or social");
        }
    }

    private void validateStatus(String status) {
        if (status != null &&
                !List.of("scheduled", "cancelled", "completed").contains(status)) {
            throw new IllegalArgumentException("Invalid status. Must be: scheduled, cancelled, or completed");
        }
    }
}