package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.DoctorSignupInputDTO;
import com.scheduler.schedulerapp.dto.PatientSignupInputDTO;
import com.scheduler.schedulerapp.dto.SignupResponseDTO;
import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import com.scheduler.schedulerapp.service.doctor.DoctorService;
import com.scheduler.schedulerapp.service.patient.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
public class SignupResolver {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MutationMapping
    public SignupResponseDTO signupDoctor(@Valid @Argument("input") DoctorSignupInputDTO input) {
        try {
            if (!input.getPassword().equals(input.getConfirmPassword())) {
                return new SignupResponseDTO("Passwords do not match", false, null, null, null);
            }

            if (doctorRepository.findByEmail(input.getEmail()).isPresent() ||
                    patientRepository.findByEmail(input.getEmail()).isPresent()) {
                return new SignupResponseDTO("Email already registered", false, null, null, null);
            }

            HospitalStaff doctor = new HospitalStaff();
            doctor.setName(input.getName().trim());
            doctor.setEmail(input.getEmail().toLowerCase().trim());
            doctor.setPassword(passwordEncoder.encode(input.getPassword()));
            doctor.setRole("doctor");

            HospitalStaff savedDoctor = doctorService.createDoctor(doctor);

            return new SignupResponseDTO(
                    "Doctor registered successfully! Please log in.",
                    true,
                    savedDoctor.getId(),
                    savedDoctor.getEmail(),
                    savedDoctor.getRole()
            );

        } catch (Exception e) {
            System.err.println("Error in signupDoctor: " + e.getMessage());
            e.printStackTrace();
            return new SignupResponseDTO("Registration failed: " + e.getMessage(), false, null, null, null);
        }
    }

    @MutationMapping
    public SignupResponseDTO signupPatient(@Valid @Argument("input") PatientSignupInputDTO input) {
        try {
            if (!input.getPassword().equals(input.getConfirmPassword())) {
                return new SignupResponseDTO("Passwords do not match", false, null, null, null);
            }

            if (doctorRepository.findByEmail(input.getEmail()).isPresent() ||
                    patientRepository.findByEmail(input.getEmail()).isPresent()) {
                return new SignupResponseDTO("Email already registered", false, null, null, null);
            }

            Patient patient = new Patient();
            patient.setName(input.getName().trim());
            patient.setEmail(input.getEmail().toLowerCase().trim());
            patient.setPassword(passwordEncoder.encode(input.getPassword()));
            patient.setPhoneNumber(input.getPhoneNumber().trim());
            patient.setAge(input.getAge());
            patient.setRole("patient");

            Patient savedPatient = patientService.createPatient(patient);

            return new SignupResponseDTO(
                    "Patient registered successfully! Please log in.",
                    true,
                    savedPatient.getId(),
                    savedPatient.getEmail(),
                    savedPatient.getRole()
            );

        } catch (Exception e) {
            System.err.println("Error in signupPatient: " + e.getMessage());
            e.printStackTrace();
            return new SignupResponseDTO("Registration failed: " + e.getMessage(), false, null, null, null);
        }
    }
}