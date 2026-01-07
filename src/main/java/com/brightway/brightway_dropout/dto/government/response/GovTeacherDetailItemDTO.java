package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovTeacherDetailItemDTO {
    private UUID teacherId;
    private String name;
    private String specialization;
    private int coursesTeaching;
    private List<String> courseNames;
    private int totalStudents;
}
