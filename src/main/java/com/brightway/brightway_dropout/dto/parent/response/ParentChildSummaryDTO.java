package com.brightway.brightway_dropout.dto.parent.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentChildSummaryDTO {
    private String name;
    private double overallAttendance;
    private double gpa;
    private double todayAttendance;
    private int behaviorIncidents;
    private List<WeekAttendanceDTO> attendanceTrends;
    private List<ChildBehaviorDTO> behaviorDetails;
}
