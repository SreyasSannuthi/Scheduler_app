package com.scheduler.schedulerapp.service.activitylogservice;

import com.scheduler.schedulerapp.model.ActivityLog;
import com.scheduler.schedulerapp.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ActivityLogServiceImpl {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logDoctorDeactivation(String doctorId, String doctorName, String performedBy, String performedByName, String impactSummary) {
        ActivityLog log = new ActivityLog();
        log.setEntityType("DOCTOR");
        log.setEntityId(doctorId);
        log.setActionType("DEACTIVATED");
        log.setDescription("Dr. " + doctorName + " deactivated");
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());
        log.setImpactSummary(impactSummary);

        Map<String, Object> newState = new HashMap<>();
        newState.put("isActive", false);
        newState.put("endDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd yyyy '-' h:mm a")));
        log.setNewState(newState);

        activityLogRepository.save(log);
    }

    public void logDoctorReactivation(String doctorId, String doctorName, String performedBy, String performedByName) {
        ActivityLog log = new ActivityLog();
        log.setEntityType("DOCTOR");
        log.setEntityId(doctorId);
        log.setActionType("REACTIVATED");
        log.setDescription("Dr. " + doctorName + " reactivated");
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());

        Map<String, Object> newState = new HashMap<>();
        newState.put("isActive", true);
        newState.put("endDate", "");
        log.setNewState(newState);

        activityLogRepository.save(log);
    }

    public void logBranchDeactivation(String branchId, String branchCode, String performedBy, String performedByName, String impactSummary) {
        ActivityLog log = new ActivityLog();
        log.setEntityType("HOSPITAL_BRANCH");
        log.setEntityId(branchId);
        log.setActionType("DEACTIVATED");
        log.setDescription("Branch " + branchCode + " deactivated");
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());
        log.setImpactSummary(impactSummary);

        activityLogRepository.save(log);
    }

    public void logMappingCreated(String mappingId, String doctorId, String doctorName, String branchId, String branchCode, String performedBy, String performedByName) {
        ActivityLog log = new ActivityLog();
        log.setEntityType("STAFF_BRANCH_MAPPING");
        log.setEntityId(mappingId);
        log.setActionType("MAPPING_CREATED");
        log.setDescription("Dr. " + doctorName + " assigned to " + branchCode);
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());

        Map<String, String> relatedEntities = new HashMap<>();
        relatedEntities.put("doctorId", doctorId);
        relatedEntities.put("branchId", branchId);
        log.setRelatedEntities(relatedEntities);

        activityLogRepository.save(log);
    }

    public void logMappingRemoved(String doctorId, String doctorName, String branchId, String branchCode, String performedBy, String performedByName) {
        ActivityLog log = new ActivityLog();
        log.setEntityType("STAFF_BRANCH_MAPPING");
        log.setEntityId(doctorId + "-" + branchId); // Composite ID
        log.setActionType("MAPPING_REMOVED");
        log.setDescription("Dr. " + doctorName + " removed from " + branchCode);
        log.setPerformedBy(performedBy);
        log.setPerformedByName(performedByName);
        log.setTimestamp(LocalDateTime.now());

        Map<String, String> relatedEntities = new HashMap<>();
        relatedEntities.put("doctorId", doctorId);
        relatedEntities.put("branchId", branchId);
        log.setRelatedEntities(relatedEntities);

        activityLogRepository.save(log);
    }
}
