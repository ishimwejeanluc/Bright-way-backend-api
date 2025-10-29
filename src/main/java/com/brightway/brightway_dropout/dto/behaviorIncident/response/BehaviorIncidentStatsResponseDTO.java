package com.brightway.brightway_dropout.dto.behaviorIncident.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorIncidentStatsResponseDTO {
    private int totalReports;
    private int totalMajorIncidents;
    private int totalMinorIncidents;
    private List<BehaviorIncidentReportDTO> reports;
}