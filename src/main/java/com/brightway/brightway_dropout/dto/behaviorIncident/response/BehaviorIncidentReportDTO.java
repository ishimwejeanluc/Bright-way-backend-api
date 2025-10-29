package com.brightway.brightway_dropout.dto.behaviorIncident.response;

import com.brightway.brightway_dropout.enumeration.EIncidentType;
import com.brightway.brightway_dropout.enumeration.ESeverityLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorIncidentReportDTO {
    private String studentName;
    private ESeverityLevel severityLevel;
    private EIncidentType incidentType;
    private String notes;
}