package com.brightway.brightway_dropout.dto.prediction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactorDTO {
    
    private String factor;
    private String message;
    private String severity;  // critical, warning, positive
}
