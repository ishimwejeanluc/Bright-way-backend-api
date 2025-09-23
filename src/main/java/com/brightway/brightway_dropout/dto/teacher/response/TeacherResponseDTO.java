package com.brightway.brightway_dropout.dto.teacher.response;

import com.brightway.brightway_dropout.dto.school.response.SchoolResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeacherResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private SchoolResponseDTO school;
    private List<CourseResponseDTO> courses;
}
