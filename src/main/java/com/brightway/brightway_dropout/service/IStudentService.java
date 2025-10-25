package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.student.request.CreateStudentWithParentRequestDTO;
import com.brightway.brightway_dropout.dto.student.response.CreateStudentWithParentResponseDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentDetailDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentStatsResponseDTO;
import java.util.List;
import java.util.UUID;

public interface IStudentService {
    StudentStatsResponseDTO getStudentStatsBySchool(UUID schoolId);
    CreateStudentWithParentResponseDTO createStudentWithParent(CreateStudentWithParentRequestDTO dto);
    List<StudentDetailDTO> getStudentsByCourse(UUID courseId);
}
