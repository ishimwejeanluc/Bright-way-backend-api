package com.brightway.brightway_dropout.dto.course.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailDTO {
    private String name;
    private String grade;
    private int credits;
    private String teacherName;
    private boolean active;
    private int enrollmentCount;
    private int atRiskStudents;
}
