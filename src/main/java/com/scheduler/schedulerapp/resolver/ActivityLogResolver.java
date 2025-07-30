package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.ActivityLogResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.model.ActivityLog;
import com.scheduler.schedulerapp.service.activitylogservice.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ActivityLogResolver {

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public List<ActivityLogResponseDTO> getActivityLogs() {
        List<ActivityLog> log = activityLogService.getActivityLogs();
        return log.stream()
                .map(dtoMapper::toActivityLogResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<ActivityLogResponseDTO> getActivityLogsByType(@Argument String entityType) {
        List<ActivityLog> log = activityLogService.getActivityLogsByType(entityType);
        return log.stream()
                .map(dtoMapper::toActivityLogResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<ActivityLogResponseDTO> getEntityActivityById(@Argument String entityType,@Argument String entityId) {
        List<ActivityLog> log = activityLogService.getEntityActivityById(entityType, entityId);
        return log.stream()
                .map(dtoMapper::toActivityLogResponseDTO)
                .collect(Collectors.toList());
    }
}
