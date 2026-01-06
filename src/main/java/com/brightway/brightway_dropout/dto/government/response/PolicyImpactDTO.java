package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyImpactDTO {
    private String schoolId;
    private String schoolName;
    private double attendance;
    private double performance;
}
