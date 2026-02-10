package com.brightway.brightway_dropout.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.List;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceStudentOverviewDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.StBehaviorIncidentDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StAttendanceOverviewDTO {
    private double overallAttendancePercentage;
    private Map<String, Double> weeklyAttendancePercentages; // week label -> percentage
    private Map<String, Double> courseAttendancePercentages; // course name -> percentage
    private String mostMissedClassName;
    private int mostMissedClassTotal;
    private List<AttendanceStudentOverviewDTO> attendanceOverview; // Daily attendance data
    private List<StBehaviorIncidentDTO> behaviorIncidents; // All behavior incidents
}
