package com.scheduler.schedulerapp.dto.recordtabdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthAnalyticsResponseDTO {
    private Integer totalVisits;
    private String lastVisitDate;
    private List<String> commonDiagnoses;
    private Integer missedAppointments;
    private Map<String, Double> weightHistory = new HashMap<>();
    private Double averageVisitsPerMonth;
    private String lastAnalyticsUpdate;
}