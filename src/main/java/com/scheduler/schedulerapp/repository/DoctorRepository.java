package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.Doctor;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository <Doctor, String> {

    List<Doctor> findByRole(String role);
    Optional<Doctor> findByEmail(String email);

}
