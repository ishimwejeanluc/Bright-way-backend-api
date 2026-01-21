package com.brightway.brightway_dropout.dto.teacher.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAttendanceDTO {
    private String dayName;
    private LocalDate date;
    private int presentCount;
    private int absentCount;
}
