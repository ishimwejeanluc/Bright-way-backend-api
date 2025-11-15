package com.brightway.brightway_dropout.dto.student.response;

import com.brightway.brightway_dropout.dto.grade.response.StudentPerformanceTrendDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceStudentOverviewDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDashboardDTO {
    private double attendanceRate;
    private double averageGPA;
    private int behaviorIncidents;
    private String riskLevel; // latest risk level
    private Float probabilityPercent; // latest dropout probability in percent
    private List<StudentPerformanceTrendDTO> performanceTrend;
    private List<AttendanceStudentOverviewDTO> attendanceOverview;
}
