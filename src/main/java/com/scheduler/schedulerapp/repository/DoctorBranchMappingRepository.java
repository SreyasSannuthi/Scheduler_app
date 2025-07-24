package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.DoctorBranchMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorBranchMappingRepository extends MongoRepository<DoctorBranchMapping, String> {
    List<DoctorBranchMapping> findByDoctorId(String doctorId);
    List<DoctorBranchMapping> findByBranchId(String branchId);
    Optional<DoctorBranchMapping> findByDoctorIdAndBranchId(String doctorId, String branchId);
    void deleteByDoctorIdAndBranchId(String doctorId, String branchId);
}
