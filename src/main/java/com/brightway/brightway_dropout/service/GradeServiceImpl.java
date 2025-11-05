
package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.grade.request.RegisterGradeBulkDTO;
import com.brightway.brightway_dropout.dto.grade.request.StudentGradeDTO;
import com.brightway.brightway_dropout.dto.grade.response.RegisterGradeBulkResponseDTO;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.Enrollment;
import com.brightway.brightway_dropout.model.Grade;
import com.brightway.brightway_dropout.model.Teacher;
import com.brightway.brightway_dropout.repository.IEnrollmentRepository;
import com.brightway.brightway_dropout.repository.IGradeRepository;
import com.brightway.brightway_dropout.dto.grade.response.StudentGradesByTeacherResponseDTO;
import com.brightway.brightway_dropout.dto.grade.response.StudentGradeDetailDTO;
import com.brightway.brightway_dropout.repository.ITeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements IGradeService {

   
    private final IGradeRepository gradeRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final ITeacherRepository teacherRepository;

    @Override
    public RegisterGradeBulkResponseDTO registerGrades(RegisterGradeBulkDTO dto) {
        List<UUID> savedIds = new ArrayList<>();
        for (StudentGradeDTO studentGrade : dto.getGrades()) {
            // Find enrollment by studentId and courseId
            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(
                    studentGrade.getStudentId(), 
                    dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Student with ID " + studentGrade.getStudentId() + " not found in course " + dto.getCourseId()));
            
            Grade grade = new Grade();
            grade.setEnrollment(enrollment);
            grade.setName(dto.getGradeName());
            grade.setGradeType(dto.getGradeType());
            grade.setMarks(studentGrade.getMarks());
            Grade saved = gradeRepository.save(grade);
            savedIds.add(saved.getId());
        }
        return new RegisterGradeBulkResponseDTO(
            savedIds, 
            savedIds.size(),
             "Grades registered successfully");
    }
    @Override
    public List<StudentGradesByTeacherResponseDTO> getStudentGradesByTeacherUserId(UUID userId) {
        UUID teacherId = getTeacherId(userId);
        List<Object[]> rows = gradeRepository.findStudentGradesByTeacherId(teacherId);
        // Group by course name
        Map<String, List<StudentGradeDetailDTO>> grouped = new HashMap<>();
        for (Object[] row : rows) {
            String courseName = row[0] != null ? row[0].toString() : "";
            UUID studentId = row[1] != null ? UUID.fromString(row[1].toString()) : null;
            String studentName = row[2] != null ? row[2].toString() : "";
            String markName = row[3] != null ? row[3].toString() : "";
            Double marks = row[4] != null ? Double.valueOf(row[4].toString()) : null;
            String gradeType = row[5] != null ? row[5].toString() : "";
            StudentGradeDetailDTO detail = new StudentGradeDetailDTO(studentId, studentName, markName, marks, gradeType);
            grouped.computeIfAbsent(courseName, k -> new ArrayList<>()).add(detail);
        }
        List<StudentGradesByTeacherResponseDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<StudentGradeDetailDTO>> entry : grouped.entrySet()) {
            result.add(new StudentGradesByTeacherResponseDTO(entry.getKey(), entry.getValue()));
        }
        return result;
    }
    public UUID getTeacherId(UUID userId){
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        return teacher.getId();
    }
}
