package com.brightway.brightway_dropout.dto.course.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatsResponseDTO {
    private int totalCourses;
    private int totalActiveCourses;
    private int totalInactiveCourses;
    private List<CourseDetailDTO> courses;
}
