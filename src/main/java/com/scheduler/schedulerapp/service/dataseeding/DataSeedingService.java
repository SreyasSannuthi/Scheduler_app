package com.scheduler.schedulerapp.service.dataseeding;

import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.model.Patient;
import com.scheduler.schedulerapp.model.HospitalBranch;
import com.scheduler.schedulerapp.model.StaffBranchMapping;
import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import com.scheduler.schedulerapp.repository.HospitalBranchRepository;
import com.scheduler.schedulerapp.repository.DoctorBranchMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        private HospitalBranchRepository hospitalBranchRepository;

        @Autowired
        private DoctorBranchMappingRepository doctorBranchMappingRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @EventListener(ApplicationReadyEvent.class)
        public void seedData() {
                seedHospitalBranches();
                seedDoctors();
                seedPatients();
                seedDoctorBranchMappings();
        }

        private void seedHospitalBranches() {
                if (hospitalBranchRepository.count() == 0) {
                        List<HospitalBranch> branches = Arrays.asList(
                                        new HospitalBranch(null, "CHN001", "123 Anna Salai, T. Nagar", "Chennai",
                                                        "Tamil Nadu",
                                                        "600017", "chennai@hospital.com", "044-12345678",
                                                        LocalDateTime.now(), "", true),

                                        new HospitalBranch(null, "BLR001", "456 MG Road, Brigade Road", "Bangalore",
                                                        "Karnataka",
                                                        "560001", "bangalore@hospital.com", "080-87654321",
                                                        LocalDateTime.now(), "", true),

                                        new HospitalBranch(null, "HYD001", "789 Banjara Hills, Road No 12", "Hyderabad",
                                                        "Telangana",
                                                        "500034", "hyderabad@hospital.com", "040-11223344",
                                                        LocalDateTime.now(), "", true),

                                        new HospitalBranch(null, "MUM001", "321 Marine Drive, Nariman Point", "Mumbai",
                                                        "Maharashtra",
                                                        "400021", "mumbai@hospital.com", "022-55667788",
                                                        LocalDateTime.now(), "", true),

                                        new HospitalBranch(null, "DEL001", "654 Connaught Place, Central Delhi",
                                                        "New Delhi", "Delhi",
                                                        "110001", "delhi@hospital.com", "011-99887766",
                                                        LocalDateTime.now(), "", true));

                        hospitalBranchRepository.saveAll(branches);
                        System.out.println(branches.size() + " hospital branches added successfully");

                        branches.forEach(branch -> System.out
                                        .println(branch.getBranchCode() + " - " + branch.getCity() + " Branch - "
                                                        + branch.getPhoneNumber()));
                } else {
                        System.out.println("Hospital branches already exist");
                }
        }

        private void seedDoctors() {
                if (doctorRepository.count() == 0) {
                        List<HospitalStaff> doctors = Arrays.asList(
                                        new HospitalStaff(null, "Sreyas Sannuthi", "admin@gmail.com", "admin",
                                                        passwordEncoder.encode("admin123"),
                                                        LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Nithin Kumar", "nithin@gmail.com", "doctor",
                                                        passwordEncoder.encode("doctor123"),
                                                        LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Kuladeep Reddy", "kuladeep@gmail.com", "doctor",
                                                        passwordEncoder.encode("doctor123"),
                                                        LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Manish Reddy", "manish@gmail.com", "doctor",
                                                        passwordEncoder.encode("doctor123"),
                                                        LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Nikhil Sai", "nikhil@gmail.com", "doctor",
                                                        passwordEncoder.encode("doctor123"),
                                                        LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Rajesh Sharma", "rajesh@gmail.com", "doctor",
                                                        passwordEncoder.encode("doctor123"),
                                                        LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Priya Patel", "priya@gmail.com", "doctor",
                                                        passwordEncoder.encode("doctor123"),
                                                        LocalDateTime.now()
                                                                        .format(DateTimeFormatter.ofPattern(
                                                                                        "MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Sarah Support", "support1@gmail.com", "customer_care",
                                                         passwordEncoder.encode("support123"),
                                                         LocalDateTime.now().
                                                                 format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")),
                                                        "", true),

                                        new HospitalStaff(null, "Mike Care", "support2@gmail.com", "customer_care",
                                                        passwordEncoder.encode("support123"),
                                                        LocalDateTime.now().
                                                                format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")),
                                                        "", true),
                                        new HospitalStaff(null, "Alice Reception", "reception1@gmail.com", "receptionist",
                                                passwordEncoder.encode("reception123"),
                                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")),
                                                "", true),
                                        new HospitalStaff(null, "Bob Front Desk", "reception2@gmail.com", "receptionist",
                                                passwordEncoder.encode("reception123"),
                                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")),
                                                "", true),
                                        new HospitalStaff(null, "Carol Welcome", "reception3@gmail.com", "receptionist",
                                                passwordEncoder.encode("reception123"),
                                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")),
                                                "", true),
                                        new HospitalStaff(null, "David Check-in", "reception4@gmail.com", "receptionist",
                                                passwordEncoder.encode("reception123"),
                                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")),
                                                "", true),
                                        new HospitalStaff(null, "Eva Help Desk", "reception5@gmail.com", "receptionist",
                                                passwordEncoder.encode("reception123"),
                                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")),
                                                "", true)
                        );

                        doctorRepository.saveAll(doctors);
                        System.out.println(doctors.size() + " doctors added successfully");

                        doctors.forEach(doctor -> System.out
                                        .println(doctor.getName() + " (" + doctor.getRole() + ") - "
                                                        + doctor.getEmail()));
                } else {
                        System.out.println("Doctors already exist");
                }
        }

        private void seedPatients() {
                if (patientRepository.count() == 0) {
                        List<Patient> patients = Arrays.asList(
                                        new Patient(null, "Vibhor Gupta", "vibhor@gmail.com", "9876543210", 27,
                                                        "patient",
                                                        passwordEncoder.encode("patient123")),
                                        new Patient(null, "Arnav Saharan", "arnav@gmail.com", "9123456780", 30,
                                                        "patient",
                                                        passwordEncoder.encode("patient123")),
                                        new Patient(null, "Mayank Pal", "mayank@gmail.com", "9012345678", 26, "patient",
                                                        passwordEncoder.encode("patient123")),
                                        new Patient(null, "Shubradip Saha", "shubradip@gmail.com", "7890123456", 24,
                                                        "patient",
                                                        passwordEncoder.encode("patient123")),
                                        new Patient(null, "Prateek Jain", "prateek@gmail.com", "8001234567", 28,
                                                        "patient",
                                                        passwordEncoder.encode("patient123")),
                                        new Patient(null, "Ankit Verma", "ankit@gmail.com", "9988776655", 32, "patient",
                                                        passwordEncoder.encode("patient123")));

                        patientRepository.saveAll(patients);
                        System.out.println(patients.size() + " patients added successfully");

                        patients.forEach(patient -> System.out
                                        .println(patient.getName() + " (" + patient.getRole() + ") - "
                                                        + patient.getEmail() +
                                                        ", Mobile: " + patient.getPhoneNumber() + ", Age: "
                                                        + patient.getAge()));
                } else {
                        System.out.println("Patients already exist");
                }
        }

        private void seedDoctorBranchMappings() {
                if (doctorBranchMappingRepository.count() == 0) {

                        List<HospitalStaff> allDoctors = doctorRepository.findAll();
                        List<HospitalBranch> allBranches = hospitalBranchRepository.findAll();

                        List<StaffBranchMapping> mappings = Arrays.asList(
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "nithin@gmail.com"),
                                                getBranchIdByCode(allBranches, "CHN001"), "Nithin Kumar",
                                                "CHN001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "nithin@gmail.com"),
                                                getBranchIdByCode(allBranches, "BLR001"), "Nithin Kumar",
                                                "BLR001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "kuladeep@gmail.com"),
                                                getBranchIdByCode(allBranches, "HYD001"), "Kuladeep Reddy",
                                                "HYD001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "kuladeep@gmail.com"),
                                                getBranchIdByCode(allBranches, "MUM001"), "Kuladeep Reddy",
                                                "MUM001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "manish@gmail.com"),
                                                getBranchIdByCode(allBranches, "DEL001"), "Manish Reddy",
                                                "DEL001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "nikhil@gmail.com"),
                                                getBranchIdByCode(allBranches, "BLR001"), "Nikhil Sai",
                                                "BLR001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "rajesh@gmail.com"),
                                                getBranchIdByCode(allBranches, "MUM001"), "Rajesh Sharma",
                                                "MUM001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "priya@gmail.com"),
                                                getBranchIdByCode(allBranches, "CHN001"), "Priya Patel",
                                                "CHN001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "reception1@gmail.com"),
                                                getBranchIdByCode(allBranches, "CHN001"), "Alice Reception", "CHN001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "reception2@gmail.com"),
                                                getBranchIdByCode(allBranches, "BLR001"), "Bob Front Desk", "BLR001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "reception3@gmail.com"),
                                                getBranchIdByCode(allBranches, "HYD001"), "Carol Welcome", "HYD001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "reception4@gmail.com"),
                                                getBranchIdByCode(allBranches, "MUM001"), "David Check-in", "MUM001"),
                                        new StaffBranchMapping(null,
                                                getDoctorIdByEmail(allDoctors, "reception5@gmail.com"),
                                                getBranchIdByCode(allBranches, "DEL001"), "Eva Help Desk", "DEL001")
                        );

                        mappings = mappings.stream()
                                        .filter(mapping -> mapping.getDoctorId() != null
                                                        && mapping.getBranchId() != null)
                                        .toList();

                        doctorBranchMappingRepository.saveAll(mappings);
                        System.out.println(mappings.size() + " doctor-branch mappings created successfully");

                        mappings.forEach(mapping -> System.out.println(
                                        "Mapping created: " + mapping.getDoctorId() + " -> " + mapping.getBranchId()));
                } else {
                        System.out.println("Doctor-branch mappings already exist");
                }
        }

        private String getDoctorIdByEmail(List<HospitalStaff> doctors, String email) {
                return doctors.stream()
                                .filter(doctor -> email.equals(doctor.getEmail()))
                                .map(HospitalStaff::getId)
                                .findFirst()
                                .orElse(null);
        }

        private String getBranchIdByCode(List<HospitalBranch> branches, String branchCode) {
                return branches.stream()
                                .filter(branch -> branchCode.equals(branch.getBranchCode()))
                                .map(HospitalBranch::getId)
                                .findFirst()
                                .orElse(null);
        }
}