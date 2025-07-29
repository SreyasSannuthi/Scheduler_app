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

    private String description;

    private String performedBy;

    private String performedByName;

    @Indexed
    private LocalDateTime timestamp;

    private Map<String, Object> previousState;

    private Map<String, Object> newState;

    private Map<String, String> relatedEntities;

    private String impactSummary;
}