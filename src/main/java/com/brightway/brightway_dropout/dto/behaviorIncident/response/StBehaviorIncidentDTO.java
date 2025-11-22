package com.brightway.brightway_dropout.dto.behaviorIncident.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StBehaviorIncidentDTO {
    private String note;
        private String incidentType;
        private String severityLevel;
}
