package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.student.request.CreateStudentWithParentRequestDTO;
import com.brightway.brightway_dropout.dto.student.response.CreateStudentWithParentResponseDTO;
import com.brightway.brightway_dropout.dto.student.response.StudentStatsResponseDTO;
import com.brightway.brightway_dropout.service.IStudentService;
import com.brightway.brightway_dropout.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final IStudentService studentService;

    @PostMapping("/create-with-parent")
    public ResponseEntity<ApiResponse> createStudentWithParent(@RequestBody CreateStudentWithParentRequestDTO dto) {
        CreateStudentWithParentResponseDTO response = studentService.createStudentWithParent(dto);
        return new ResponseEntity<>(
            new ApiResponse(true, "Student and parent registered successfully", response),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/stats/by-school/{schoolId}")
    public ResponseEntity<ApiResponse> getStudentStatsBySchool(@PathVariable UUID schoolId) {
        StudentStatsResponseDTO response = studentService.getStudentStatsBySchool(schoolId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Student stats retrieved successfully", response),
            HttpStatus.OK
        );
    }
}
