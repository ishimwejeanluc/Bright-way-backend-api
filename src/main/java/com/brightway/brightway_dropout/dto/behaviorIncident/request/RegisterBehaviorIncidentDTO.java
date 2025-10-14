package com.brightway.brightway_dropout.dto.behaviorIncident.request;

import com.brightway.brightway_dropout.enumeration.EIncidentType;
import com.brightway.brightway_dropout.enumeration.ESeverityLevel;
import lombok.Data;
import java.util.UUID;

@Data
public class RegisterBehaviorIncidentDTO {
    private UUID studentId;
    private String notes;
    private EIncidentType incidentType;
    private ESeverityLevel severity;
}
