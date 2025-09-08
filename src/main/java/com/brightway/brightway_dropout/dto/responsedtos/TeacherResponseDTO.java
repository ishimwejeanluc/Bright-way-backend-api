package com.brightway.brightway_dropout.dto.responsedtos;

import com.brightway.brightway_dropout.enumeration.EUserRole;
import com.brightway.brightway_dropout.model.Course;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeacherResponseDTO {
    private UUID id;
    private String name;


}
