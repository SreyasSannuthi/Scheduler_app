package com.scheduler.schedulerapp.service.dataseeding;

import com.scheduler.schedulerapp.model.Doctor;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Profile("!test")
public class DataSeedingService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {
        seedDoctors();
        seedPatients();
    }

    private void seedDoctors() {
        if (doctorRepository.count() == 0) {
            List<Doctor> doctors = Arrays.asList(
                    new Doctor(null, "Sreyas Sannuthi", "admin@gmail.com", "admin", passwordEncoder.encode("admin123")),
                    new Doctor(null, "Nithin Kumar", "nithin@gmail.com", "doctor", passwordEncoder.encode("doctor123")),
                    new Doctor(null, "Kuladeep Reddy", "kuladeep@gmail.com", "doctor", passwordEncoder.encode("doctor123")),
                    new Doctor(null, "Manish Reddy", "manish@gmail.com", "doctor", passwordEncoder.encode("doctor123")),
                    new Doctor(null, "Nikhil Sai", "nikhil@gmail.com", "doctor", passwordEncoder.encode("doctor123"))
            );

            doctorRepository.saveAll(doctors);
            System.out.println(doctors.size() + " doctors added successfully");

            doctors.forEach(doctor -> System.out
                    .println(doctor.getName() + " (" + doctor.getRole() + ") - " + doctor.getEmail()));
        } else {
            System.out.println("Doctors already exist");
        }
    }

    private void seedPatients() {
        if (patientRepository.count() == 0) {
            List<Patient> patients = Arrays.asList(
                    new Patient(null, "Vibhor Gupta", "vibhor@gmail.com", "9876543210", 27, "patient", passwordEncoder.encode("patient123")),
                    new Patient(null, "Arnav Saharan", "arnav@gmail.com", "9123456780", 30, "patient", passwordEncoder.encode("patient123")),
                    new Patient(null, "Mayank Pal", "mayank@gmail.com", "9012345678", 26, "patient", passwordEncoder.encode("patient123")),
                    new Patient(null, "Shubradip Saha", "shubradip@gmail.com", "7890123456", 24, "patient", passwordEncoder.encode("patient123")),
                    new Patient(null, "Prateek Jain", "prateek@gmail.com", "8001234567", 28, "patient", passwordEncoder.encode("patient123"))
            );

            patientRepository.saveAll(patients);
            System.out.println(patients.size() + " patients added successfully");

            patients.forEach(patient -> System.out
                    .println(patient.getName() + " (" + patient.getRole() + ") - " + patient.getEmail() +
                            ", Mobile: " + patient.getPhoneNumber() + ", Age: " + patient.getAge()));
        } else {
            System.out.println("Patients already exist");
        }
    }
}