package com.brightway.brightway_dropout.dto.teacher.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDashboardStatsDTO {
    private int totalStudents;
    private int totalCourses;
    private double todayAttendancePercentage;
    private int atRiskStudents;
}
