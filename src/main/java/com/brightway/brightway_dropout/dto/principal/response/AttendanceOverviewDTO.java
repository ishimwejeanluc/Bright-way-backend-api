package com.brightway.brightway_dropout.dto.principal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceOverviewDTO {
    private Integer thisMonthPercent;
    private Integer thisSemesterPercent;
    private Integer thisYearPercent;
}
