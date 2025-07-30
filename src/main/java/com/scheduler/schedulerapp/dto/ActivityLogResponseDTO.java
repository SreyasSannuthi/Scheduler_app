package com.scheduler.schedulerapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogResponseDTO {
    private String id;
    private String entityType;
    private String entityId;
    private String actionType;
    private String description;
    private String performedBy;
    private String performedByName;
    private String timestamp;
    private String state;
    private String relatedEntities;
    private String impactSummary;
    private String staffRole;
    private String staffName;
    private String branchCode;
    private String branchLocation;

}