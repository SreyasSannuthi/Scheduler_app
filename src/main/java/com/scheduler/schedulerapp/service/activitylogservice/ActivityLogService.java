package com.scheduler.schedulerapp.service.activitylogservice;

import com.scheduler.schedulerapp.model.ActivityLog;
import java.util.List;

public interface ActivityLogService {

    void logDoctorDeactivation(String doctorId, String doctorName, String performedBy, String performedByName, String impactSummary);

    void logDoctorReactivation(String doctorId, String doctorName, String performedBy, String performedByName);

    void logBranchDeactivation(String branchId, String branchCode, String performedBy, String performedByName, String impactSummary);

    void logBranchReactivation(String branchId, String branchCode, String performedBy, String performedByName);

    void logMappingCreated(String mappingId, String doctorId, String doctorName, String branchId, String branchCode, String performedBy, String performedByName);

    void logMappingRemoved(String doctorId, String doctorName, String branchId, String branchCode, String performedBy, String performedByName);

    List<ActivityLog> getEntityActivityById(String entityType, String entityId);
    List<ActivityLog> getActivityLogsByType(String entityType);
    List<ActivityLog> getActivityLogs();

}