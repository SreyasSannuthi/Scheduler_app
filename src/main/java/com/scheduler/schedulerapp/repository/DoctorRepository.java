package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.HospitalStaff;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository <HospitalStaff, String> {

    @Query("{'isActive' : true}")
    List<HospitalStaff> findAllWhoIsActive();

    List<HospitalStaff> findByRole(String role);
    Optional<HospitalStaff> findByEmail(String email);
}
