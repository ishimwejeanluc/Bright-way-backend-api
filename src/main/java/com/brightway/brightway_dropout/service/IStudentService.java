package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.student.request.CreateStudentWithParentRequestDTO;
import com.brightway.brightway_dropout.dto.student.response.CreateStudentWithParentResponseDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentDetailDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentStatsResponseDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentDashboardDTO;
import com.brightway.brightway_dropout.dto.parent.response.ParentDashboardDTO;
import java.util.List;
import java.util.UUID;

public interface IStudentService {
    StudentDashboardDTO getStudentDashboard(UUID studentId);
    StudentStatsResponseDTO getStudentStatsBySchool(UUID schoolId);
    CreateStudentWithParentResponseDTO createStudentWithParent(CreateStudentWithParentRequestDTO dto);
    List<StudentDetailDTO> getStudentsByCourse(UUID courseId);
    ParentDashboardDTO getParentDashboard(UUID parentId);
}
