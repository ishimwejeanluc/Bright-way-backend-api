package com.brightway.brightway_dropout.dto.attendance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyAttendanceTrendsDTO {
    private List<DailyAttendanceStatsDTO> dailyStats;
    private WeeklyAveragesDTO weeklyAverages;
}