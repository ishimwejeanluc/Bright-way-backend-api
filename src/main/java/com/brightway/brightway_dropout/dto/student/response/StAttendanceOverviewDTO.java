package com.brightway.brightway_dropout.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StAttendanceOverviewDTO {
    private double overallAttendancePercentage;
    private Map<String, Double> weeklyAttendancePercentages; // week label -> percentage
    private Map<String, Double> courseAttendancePercentages; // course name -> percentage
}
