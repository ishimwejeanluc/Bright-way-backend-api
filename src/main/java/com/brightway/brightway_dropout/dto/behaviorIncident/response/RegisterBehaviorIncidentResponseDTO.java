package com.brightway.brightway_dropout.dto.behaviorIncident.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterBehaviorIncidentResponseDTO {
    private UUID behaviorId;
    private UUID studentId;
    private String message ;
}
