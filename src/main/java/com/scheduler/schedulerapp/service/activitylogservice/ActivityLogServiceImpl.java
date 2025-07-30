package com.scheduler.schedulerapp.service.activitylogservice;

import com.scheduler.schedulerapp.model.ActivityLog;
import com.scheduler.schedulerapp.model.HospitalBranch;
import com.scheduler.schedulerapp.repository.ActivityLogRepository;
import com.scheduler.schedulerapp.repository.HospitalBranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scheduler.schedulerapp.model.HospitalStaff;
import com.scheduler.schedulerapp.repository.DoctorRepository;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HospitalBranchRepository hospitalBranchRepository;


    @Override
    public void logDoctorDeactivation(String doctorId, String doctorName, String performedBy, String performedByName, String impactSummary) {

        HospitalStaff staff = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        ActivityLog log = new ActivityLog();

        log.setEntityType("HOSPITAL_STAFF");
        log.setEntityId(doctorId);
        log.setActionType("DEACTIVATED");

        log.setStaffName(staff.getName());
        String role = staff.getRole();
        log.setStaffRole(role);
        if("DOCTOR".equalsIgnoreCase(role))
            log.setDescription("Dr. " + doctorName + " deactivated");
        else
            log.setDescription(doctorName + " (" +role +")" + " deactivated");

        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());
        log.setImpactSummary(impactSummary);

        Map<String, Object> newState = new HashMap<>();
        newState.put("isActive", false);
        newState.put("deactivatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
        log.setState(newState);

        activityLogRepository.save(log);
    }

    @Override
    public void logDoctorReactivation(String doctorId, String doctorName, String performedBy, String performedByName) {

        HospitalStaff staff = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        ActivityLog log = new ActivityLog();

        log.setEntityType("HOSPITAL_STAFF");
        log.setEntityId(doctorId);
        log.setActionType("REACTIVATED");

        log.setStaffName(staff.getName());
        String role = staff.getRole();
        log.setStaffRole(role);
        if("DOCTOR".equalsIgnoreCase(role))
            log.setDescription("Dr. " + doctorName + " reactivated");
        else
            log.setDescription(doctorName + " (" +role +")" + " reactivated");
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());

        Map<String, Object> newState = new HashMap<>();
        newState.put("isActive", true);
        newState.put("reactivatedDate",LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
        log.setState(newState);

        activityLogRepository.save(log);
    }

    @Override
    public void logBranchDeactivation(String branchId, String branchCode, String performedBy, String performedByName, String impactSummary) {

        HospitalBranch branch = hospitalBranchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));

        ActivityLog log = new ActivityLog();
        log.setEntityType("HOSPITAL_BRANCH");
        log.setEntityId(branchId);
        log.setActionType("DEACTIVATED");
        log.setDescription("Branch " + branchCode + " deactivated");
        log.setBranchCode(branch.getBranchCode());
        log.setBranchLocation(branch.getCity());
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());
        log.setImpactSummary(impactSummary);

        Map<String, Object> newState = new HashMap<>();
        newState.put("isActive", false);
        newState.put("deactivatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
        log.setState(newState);

        activityLogRepository.save(log);
    }

    public void logBranchReactivation(String branchId, String branchCode, String performedBy, String performedByName) {

        HospitalBranch branch = hospitalBranchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));

        ActivityLog log = new ActivityLog();
        log.setEntityType("HOSPITAL_BRANCH");
        log.setEntityId(branchId);
        log.setActionType("REACTIVATED");
        log.setDescription("Branch " + branchCode + " reactivated");
        log.setBranchCode(branch.getBranchCode());
        log.setBranchLocation(branch.getCity());
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());

        Map<String, Object> newState = new HashMap<>();
        newState.put("isActive", false);
        newState.put("reactivatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
        log.setState(newState);

        activityLogRepository.save(log);
    }

    @Override
    public void logMappingCreated(String mappingId, String doctorId, String doctorName, String branchId, String branchCode, String performedBy, String performedByName) {

        HospitalStaff staff = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        HospitalBranch branch = hospitalBranchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));

        ActivityLog log = new ActivityLog();
        log.setEntityType("STAFF_BRANCH_MAPPING");
        log.setEntityId(mappingId);
        log.setActionType("MAPPING_CREATED");

        log.setStaffName(staff.getName());
        String role = staff.getRole();
        log.setStaffRole(role);
        if("DOCTOR".equalsIgnoreCase(role))
            log.setDescription("Dr. " + doctorName + " assigned to " + branchCode);
        else
            log.setDescription(doctorName + " (" +role +")" + " assigned to " + branchCode);

        log.setBranchCode(branch.getBranchCode());
        log.setBranchLocation(branch.getCity());

        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());

        Map<String, String> relatedEntities = new HashMap<>();
        relatedEntities.put("doctorId", doctorId);
        relatedEntities.put("branchId", branchId);
        log.setRelatedEntities(relatedEntities);

        activityLogRepository.save(log);
    }

    @Override
    public void logMappingRemoved(String doctorId, String doctorName, String branchId, String branchCode, String performedBy, String performedByName) {

        HospitalStaff staff = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        HospitalBranch branch = hospitalBranchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));

        ActivityLog log = new ActivityLog();
        log.setEntityType("STAFF_BRANCH_MAPPING");
        log.setEntityId(doctorId + "-" + branchId);
        log.setActionType("MAPPING_REMOVED");

        log.setStaffName(staff.getName());
        String role = staff.getRole();
        log.setStaffRole(role);
        if("DOCTOR".equalsIgnoreCase(role))
            log.setDescription("Dr. " + doctorName + " removed from branch " + branchCode);
        else
            log.setDescription(doctorName + " (" +role +")" + " removed from branch " + branchCode);

        log.setBranchCode(branch.getBranchCode());
        log.setBranchLocation(branch.getCity());

        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());
        Map<String, String> relatedEntities = new HashMap<>();
        relatedEntities.put("doctorId", doctorId);
        relatedEntities.put("branchId", branchId);
        log.setRelatedEntities(relatedEntities);

        activityLogRepository.save(log);
    }

    @Override
    public List<ActivityLog> getEntityActivityById(String entityType, String entityId) {
        return activityLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    @Override
    public List<ActivityLog> getActivityLogsByType(String entityType) {
        return activityLogRepository.findByEntityTypeOrderByTimestampDesc(entityType);
    }

    @Override
    public List<ActivityLog> getActivityLogs() {
        return activityLogRepository.findAllByOrderByTimestampDesc();
    }
}