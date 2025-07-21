package com.scheduler.schedulerapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.scheduler.schedulerapp.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository <Patient,String> {
    List<Patient> findByRole(String role);
    Optional<Patient> findByEmail(String email);
}
