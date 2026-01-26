package com.brightway.brightway_dropout.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseAttendanceDataDTO {
    private List<String> dates; // Column headers for the grid
    private List<AttendanceReportDTO> studentAttendance;
}
