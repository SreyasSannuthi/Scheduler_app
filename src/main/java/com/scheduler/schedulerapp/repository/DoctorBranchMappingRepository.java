package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.StaffBranchMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorBranchMappingRepository extends MongoRepository<StaffBranchMapping, String> {
    List<StaffBranchMapping> findByDoctorId(String doctorId);
    List<StaffBranchMapping> findByBranchId(String branchId);
    Optional<StaffBranchMapping> findByDoctorIdAndBranchId(String doctorId, String branchId);
    void deleteByDoctorIdAndBranchId(String doctorId, String branchId);

    int countByDoctorId(String id);
    int countByBranchId(String id);
}
