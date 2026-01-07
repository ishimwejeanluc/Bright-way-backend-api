package com.brightway.brightway_dropout.dto.government.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovTeacherOverviewItemDTO {
    private UUID schoolId;
    private String schoolName;
    private String region;
    private int numberOfTeachers;
}
