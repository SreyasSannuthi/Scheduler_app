package com.scheduler.schedulerapp.mapper;

import com.scheduler.schedulerapp.dto.AppointmentResponseDTO;
import com.scheduler.schedulerapp.dto.UserResponseDTO;
import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.model.User;
import com.scheduler.schedulerapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class DTOMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private UserRepository userRepository;

    public UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDisplayName(user.getName() + " (" + user.getRole() + ")");
        return dto;
    }

    public AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setTitle(appointment.getTitle());
        dto.setDescription(appointment.getDescription());
        dto.setUserId(appointment.getUserId());
        dto.setStartTime(appointment.getStartTime().format(ISO_FORMATTER));
        dto.setEndTime(appointment.getEndTime().format(ISO_FORMATTER));
        dto.setStatus(appointment.getStatus());
        dto.setCategory(appointment.getCategory());
        dto.setCategoryColor(getCategoryColor(appointment.getCategory()));
        dto.setDuration(calculateDuration(appointment));

        Optional<User> user = userRepository.findById(appointment.getUserId());
        dto.setUserName(user.map(User::getName).orElse("Unknown User"));

        return dto;
    }

    private String getCategoryColor(String category) {
        return switch (category) {
            case "work" -> "#3174ad";
            case "personal" -> "#28a745";
            case "medical" -> "#dc3545";
            case "education" -> "#6f42c1";
            case "social" -> "#ffc107";
            default -> "#6c757d";
        };
    }

    private String calculateDuration(Appointment appointment) {
        Duration duration = Duration.between(appointment.getStartTime(), appointment.getEndTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        if (hours == 0) {
            return minutes + " minutes";
        } else if (minutes == 0) {
            return hours + " hours";
        } else {
            return hours + " hours " + minutes + " minutes";
        }
    }
}