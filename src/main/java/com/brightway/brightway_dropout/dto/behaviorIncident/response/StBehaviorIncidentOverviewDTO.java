package com.brightway.brightway_dropout.dto.behaviorIncident.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StBehaviorIncidentOverviewDTO {
    private int totalIncidents;
    private int totalMajorIncidents;
    private int totalLowIncidents;
    private List<StBehaviorIncidentDTO> incidents;
}
