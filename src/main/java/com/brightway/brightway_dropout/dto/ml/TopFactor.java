package com.brightway.brightway_dropout.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopFactor {
    
    private String factor;      // e.g., "Low Attendance", "Failing Grades"
    private String message;     // e.g., "Attendance rate is only 45%"
    private String severity;    // "positive", "warning", "critical" - converted from ML "status"
}
