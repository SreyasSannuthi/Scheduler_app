package com.scheduler.schedulerapp.model.recordtab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthAnalytics {
    private Integer totalVisits;
    private LocalDateTime lastVisitDate;
    private List<String> commonDiagnoses = new ArrayList<>();
    private Integer missedAppointments;
    private Map<String, Double> weightHistory = new HashMap<>();
    private Double averageVisitsPerMonth;
    private LocalDateTime lastAnalyticsUpdate;
}