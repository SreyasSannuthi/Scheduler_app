package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.HospitalBranch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalBranchRepository extends MongoRepository<HospitalBranch, String> {
    List<HospitalBranch> findByIsActiveTrue();
    Optional<HospitalBranch> findByBranchCode(String branchCode);
    List<HospitalBranch> findByCity(String city);
    boolean existsByBranchCode(String branchCode);
}
