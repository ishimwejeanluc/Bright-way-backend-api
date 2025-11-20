package com.brightway.brightway_dropout.dto.student.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StCourseOverviewDTO {
    private String courseName;
    private Double currentGpa;
    private List<StCourseMarkDTO> marks;
}
