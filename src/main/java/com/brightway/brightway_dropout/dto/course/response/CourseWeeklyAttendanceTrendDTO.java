package com.brightway.brightway_dropout.dto.course.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWeeklyAttendanceTrendDTO {
    private String courseName;
    private List<Double> weeklyAttendancePercentages; // Each entry is a week's attendance %
}
