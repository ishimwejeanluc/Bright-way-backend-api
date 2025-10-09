package com.brightway.brightway_dropout.dto.teacher.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatsDTO {
    private String courseName;
    private int totalStudents;
    private double todayAttendancePercentage;
    private boolean active;
}
