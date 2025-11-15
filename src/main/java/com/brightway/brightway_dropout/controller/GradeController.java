
package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.grade.request.RegisterGradeBulkDTO;
import com.brightway.brightway_dropout.dto.grade.response.RegisterGradeBulkResponseDTO;
import com.brightway.brightway_dropout.service.IGradeService;
import com.brightway.brightway_dropout.dto.grade.response.StudentGradesByTeacherResponseDTO;
import java.util.List;
import java.util.UUID;
import com.brightway.brightway_dropout.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    

    private final IGradeService gradeService;

    @PostMapping("/bulk-register")
    public ResponseEntity<ApiResponse> registerGrades(@Valid @RequestBody RegisterGradeBulkDTO dto) {
        RegisterGradeBulkResponseDTO response = gradeService.registerGrades(dto);
        return new ResponseEntity<>(
                new ApiResponse(true, "Grades registered successfully", response),
                HttpStatus.CREATED
        );
    }
    @GetMapping("/by-teacher/{userId}")
    public ResponseEntity<ApiResponse> getStudentGradesByTeacher(@PathVariable UUID userId) {
        List<StudentGradesByTeacherResponseDTO> response = gradeService.getStudentGradesByTeacherUserId(userId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Student grades by teacher", response),
            HttpStatus.OK
        );
    }
}
