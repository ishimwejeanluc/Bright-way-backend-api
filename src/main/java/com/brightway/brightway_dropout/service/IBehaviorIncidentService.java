package com.brightway.brightway_dropout.service;

import java.util.UUID;

import com.brightway.brightway_dropout.dto.behaviorIncident.request.RegisterBehaviorIncidentDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.BehaviorIncidentStatsResponseDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.RegisterBehaviorIncidentResponseDTO;



public interface IBehaviorIncidentService {
    RegisterBehaviorIncidentResponseDTO saveIncident(RegisterBehaviorIncidentDTO dto);
    BehaviorIncidentStatsResponseDTO getBehaviorIncidentStats(UUID userId);
}
