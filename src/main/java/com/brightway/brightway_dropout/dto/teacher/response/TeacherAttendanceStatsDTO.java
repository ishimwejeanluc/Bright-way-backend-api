package com.brightway.brightway_dropout.dto.teacher.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.brightway.brightway_dropout.dto.course.response.CourseWeeklyAttendanceTrendDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherAttendanceStatsDTO {
    private int totalPresentToday;
    private int totalAbsentToday;
    private double overallAttendancePercentage;
    private List<CourseWeeklyAttendanceTrendDTO> weeklyTrends;
}
