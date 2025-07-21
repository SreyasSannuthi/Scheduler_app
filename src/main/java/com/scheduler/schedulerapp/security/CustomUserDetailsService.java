package com.scheduler.schedulerapp.security;

import com.scheduler.schedulerapp.repository.DoctorRepository;
import com.scheduler.schedulerapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) {
            return doctor.get();
        }

        var patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) {
            return patient.get();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}