package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.teacher.request.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.teacher.response.CreateTeacherResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherDetailDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherStatsResponseDTO;
import com.brightway.brightway_dropout.service.TeacherServiceImpl;
import com.brightway.brightway_dropout.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherServiceImpl teacherService;

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> updateTeacher(@PathVariable UUID id, @Valid @RequestBody CreateTeacherDTO updateTeacherDTO) {
        TeacherResponseDTO response = teacherService.updateTeacher(id, updateTeacherDTO);
        return new ResponseEntity<>(
                new ApiResponse(true,
                        "Teacher updated successfully",
                        response),
                HttpStatus.OK
        );
    }
   

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> createTeacher(@Valid @RequestBody CreateTeacherDTO createTeacherDTO) {
        CreateTeacherResponseDTO response = teacherService.createTeacher(createTeacherDTO);
        return new ResponseEntity<>(
                new ApiResponse(true,
                        "Teacher created successfully",
                        response),
                HttpStatus.CREATED
        );
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getTeacherById(@PathVariable UUID id) {
        TeacherDetailDTO response = teacherService.getTeacherById(id);
        return new ResponseEntity<>(
                new ApiResponse(true,
                        "Teacher retrieved successfully",
                        response),
                HttpStatus.OK
        );
    }

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getAllTeachers() {
        List<TeacherResponseDTO> response = teacherService.getAllTeachers();
        return new ResponseEntity<>(
                new ApiResponse(true,
                        "Teachers retrieved successfully",
                        response),
                HttpStatus.OK
        );
    }

        @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> deleteTeacher(@PathVariable UUID id) {
        DeleteResponseDTO response = teacherService.deleteTeacher(id);
        return new ResponseEntity<>(
                new ApiResponse(true,
                        response.getMessage(),
                        null),
                HttpStatus.OK
        );
    }

        @GetMapping(value = "/stats/by-school/{schoolId}", produces = MediaType.APPLICATION_JSON_VALUE)
        @PreAuthorize("hasRole('PRINCIPAL')")
        public ResponseEntity<ApiResponse> getTeacherStatsBySchool(@PathVariable UUID schoolId) {
                TeacherStatsResponseDTO response = teacherService.getTeacherStatsBySchool(schoolId);
                return new ResponseEntity<>(
                                new ApiResponse(true, "Teacher stats retrieved successfully", response),
                                HttpStatus.OK
                );
        }
}
