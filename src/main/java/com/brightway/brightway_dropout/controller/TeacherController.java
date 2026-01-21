
package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.teacher.request.CreateTeacherDTO;
import com.brightway.brightway_dropout.dto.teacher.response.CreateTeacherResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherAttendanceStatsDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherStudentListDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherDetailDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherResponseDTO;
import com.brightway.brightway_dropout.dto.teacher.response.TeacherStatsResponseDTO;
import com.brightway.brightway_dropout.service.TeacherServiceImpl;
import com.brightway.brightway_dropout.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> updateTeacher(@PathVariable UUID id, @RequestBody CreateTeacherDTO updateTeacherDTO) {
        TeacherResponseDTO response = teacherService.updateTeacher(id, updateTeacherDTO);
        return new ResponseEntity<>(
                new ApiResponse(true,
                        "Teacher updated successfully",
                        response),
                HttpStatus.OK
        );
    }


    @PostMapping(value = "/create")
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

    @GetMapping(value = "/{id}")
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

    @GetMapping
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

    @DeleteMapping(value = "/{id}")
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

    @GetMapping(value = "/stats/by-school/{schoolId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getTeacherStatsBySchool(@PathVariable UUID schoolId) {
        TeacherStatsResponseDTO response = teacherService.getTeacherStatsBySchool(schoolId);
        return new ResponseEntity<>(
                new ApiResponse(true, "Teacher stats retrieved successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/dashboard/{teacherId}")
    @PreAuthorize("hasRole('TEACHER') ")
    public ResponseEntity<ApiResponse> getTeacherDashboardStats(@PathVariable UUID teacherId) {
        var response = teacherService.getTeacherDashboardStats(teacherId);
        return new ResponseEntity<>(
                new ApiResponse(true, "Teacher dashboard stats retrieved successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/courses-stats/{teacherId}")
    @PreAuthorize("hasRole('TEACHER') ")
    public ResponseEntity<ApiResponse> getTeacherCoursesStats(@PathVariable UUID teacherId) {
        var response = teacherService.getTeacherCoursesStats(teacherId);
        return new ResponseEntity<>(
                new ApiResponse(true, "Teacher courses stats retrieved successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/attendance-stats/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getTeacherAttendanceStats(@PathVariable UUID teacherId) {
        TeacherAttendanceStatsDTO response = teacherService.getTeacherAttendanceStats(teacherId);
        return new ResponseEntity<>(
                new ApiResponse(true, "Teacher attendance stats retrieved successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/students/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getTeacherStudents(@PathVariable UUID teacherId) {
        List<TeacherStudentListDTO> response = teacherService.getTeacherStudents(teacherId);
        return new ResponseEntity<>(
                new ApiResponse(true, "Teacher students retrieved successfully", response),
                HttpStatus.OK
        );
    }

}
