
package com.brightway.brightway_dropout.service;

import java.util.List;
import java.util.UUID;
import com.brightway.brightway_dropout.dto.grade.request.RegisterGradeBulkDTO;
import com.brightway.brightway_dropout.dto.grade.response.RegisterGradeBulkResponseDTO;
import com.brightway.brightway_dropout.dto.grade.response.StudentGradesByTeacherResponseDTO;

public interface IGradeService {
    List<StudentGradesByTeacherResponseDTO> getStudentGradesByTeacherUserId(UUID teacherUserId);
    RegisterGradeBulkResponseDTO registerGrades(RegisterGradeBulkDTO dto);
}
