package com.scheduler.schedulerapp.mapper;

import com.scheduler.schedulerapp.dto.AppointmentResponseDTO;
import com.scheduler.schedulerapp.dto.DoctorResponseDTO;
import com.scheduler.schedulerapp.dto.PatientResponseDTO;
import com.scheduler.schedulerapp.model.Appointment;
import com.scheduler.schedulerapp.model.Doctor;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class DTOMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    public DoctorResponseDTO toDoctorResponseDTO(Doctor doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setEmail(doctor.getEmail());
        dto.setRole(doctor.getRole());
        return dto;
    }

    public PatientResponseDTO toPatientResponseDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setAge(patient.getAge());
        dto.setRole(patient.getRole());
        return dto;
    }

    public AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setTitle(appointment.getTitle());
        dto.setDescription(appointment.getDescription());
        dto.setDoctorId(appointment.getDoctorId());
        dto.setPatientId(appointment.getPatientId());
        dto.setStartTime(appointment.getStartTime().format(ISO_FORMATTER));
        dto.setEndTime(appointment.getEndTime().format(ISO_FORMATTER));
        dto.setStatus(appointment.getStatus());
        dto.setCreatedAt(appointment.getCreatedAt().format(ISO_FORMATTER));
        dto.setUpdatedAt(appointment.getUpdatedAt().format(ISO_FORMATTER));
        dto.setDuration(calculateDuration(appointment));

        Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctorId());
        dto.setDoctorName(doctor.map(Doctor::getName).orElse("Unknown doctor"));

        Optional<Patient> patient = patientRepository.findById(appointment.getPatientId());
        dto.setPatientName(patient.map(Patient::getName).orElse("Unknown patient"));
        return dto;
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