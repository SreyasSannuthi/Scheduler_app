package com.scheduler.schedulerapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "activityLogs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLog {
    @Id
    private String id;

    @Indexed
    private String entityType;

    @Indexed
    private String entityId;

    private String actionType;

    private String staffRole;

    private String staffName;

    private String branchCode;

    private String branchLocation;

    private String description;

    private String performedBy;

    private String performedByName;

    @Indexed
    private LocalDateTime timestamp;

    private Map<String, Object> state;

    private Map<String, String> relatedEntities;

    private String impactSummary;


    public String getStateAsString() {
        return state != null ? state.toString() : null;
    }

    public String getRelatedEntitiesAsString() {
        return relatedEntities != null ? relatedEntities.toString() : null;
    }
}