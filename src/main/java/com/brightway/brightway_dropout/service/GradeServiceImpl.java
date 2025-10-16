package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.grade.request.RegisterGradeBulkDTO;
import com.brightway.brightway_dropout.dto.grade.request.StudentGradeDTO;
import com.brightway.brightway_dropout.dto.grade.response.RegisterGradeBulkResponseDTO;
import com.brightway.brightway_dropout.enumeration.EGradeType;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.Enrollment;
import com.brightway.brightway_dropout.model.Grade;
import com.brightway.brightway_dropout.repository.IEnrollmentRepository;
import com.brightway.brightway_dropout.repository.IGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements IGradeService {
    private final IGradeRepository gradeRepository;
    private final IEnrollmentRepository enrollmentRepository;

    @Override
    public RegisterGradeBulkResponseDTO registerGrades(RegisterGradeBulkDTO dto) {
        List<UUID> savedIds = new ArrayList<>();
        for (StudentGradeDTO studentGrade : dto.getGrades()) {
            Enrollment enrollment = enrollmentRepository.findById(studentGrade.getEnrollmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found in this Course" ));
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
}
