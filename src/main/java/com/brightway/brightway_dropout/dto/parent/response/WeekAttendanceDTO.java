package com.brightway.brightway_dropout.dto.parent.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekAttendanceDTO {
    private String weekLabel; // e.g. "Week 1"
    private Double weeklyAverage; // average attendance for the week
}
