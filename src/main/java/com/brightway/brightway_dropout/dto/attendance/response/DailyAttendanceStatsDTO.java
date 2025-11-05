package com.brightway.brightway_dropout.dto.attendance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAttendanceStatsDTO {
    private String day;                 // 'Mon', 'Tue', 'Wed', etc.
    private Double averageAttendance;   // Average attendance percentage of the day for all courses
    private LocalDate date;             // LocalDate object
}