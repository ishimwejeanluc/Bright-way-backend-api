package com.brightway.brightway_dropout.service;

import java.util.UUID;

import com.brightway.brightway_dropout.dto.behaviorIncident.request.RegisterBehaviorIncidentDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.BehaviorIncidentStatsResponseDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.RegisterBehaviorIncidentResponseDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.StBehaviorIncidentOverviewDTO;


public interface IBehaviorIncidentService {
    RegisterBehaviorIncidentResponseDTO saveIncident(RegisterBehaviorIncidentDTO dto);
    BehaviorIncidentStatsResponseDTO getBehaviorIncidentStats(UUID userId);
    StBehaviorIncidentOverviewDTO getStudentBehaviorIncidentOverview(UUID studentId);
}
