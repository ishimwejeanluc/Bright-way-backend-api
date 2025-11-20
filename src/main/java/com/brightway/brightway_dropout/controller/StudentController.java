package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.student.request.CreateStudentWithParentRequestDTO;
import com.brightway.brightway_dropout.dto.student.response.*;
import com.brightway.brightway_dropout.service.IAttendanceService;
import com.brightway.brightway_dropout.service.ICourseService;
import com.brightway.brightway_dropout.service.IStudentService;
import com.brightway.brightway_dropout.util.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final IStudentService studentService;
    private final IAttendanceService attendanceService;
    private final ICourseService courseService;

    @GetMapping("/{studentId}/course-overview")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getStudentCourseOverview(@PathVariable UUID studentId) {
        List<StCourseOverviewDTO> response = courseService.getStudentCourseOverview(studentId);
        return new ResponseEntity<>(new ApiResponse(true, "Course overview loaded", response), HttpStatus.OK);
    }
    
    @GetMapping("/{studentId}/attendance-overview")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getStudentAttendanceOverview(@PathVariable UUID studentId) {
        StAttendanceOverviewDTO response = attendanceService.getStudentAttendanceOverview(studentId);
        return new ResponseEntity<>(new ApiResponse(true, "Attendance overview loaded", response), 
        HttpStatus.OK);
    }

    @PostMapping("/create-with-parent")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> createStudentWithParent(@Valid @RequestBody CreateStudentWithParentRequestDTO dto) {
        CreateStudentWithParentResponseDTO response = studentService.createStudentWithParent(dto);
        return new ResponseEntity<>(
            new ApiResponse(true, "Student and parent registered successfully", response),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/stats/by-school/{schoolId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getStudentStatsBySchool(@PathVariable UUID schoolId) {
        StudentStatsResponseDTO response = studentService.getStudentStatsBySchool(schoolId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Student stats retrieved successfully", response),
            HttpStatus.OK
        );
    }

    @GetMapping("/by-course/{courseId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getStudentsByCourse(@PathVariable UUID courseId) {
        List<StudentDetailDTO> response = studentService.getStudentsByCourse(courseId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Students retrieved successfully", response),
            HttpStatus.OK
        );
    }
    
    @GetMapping("/dashboard/{studentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getStudentDashboard(@PathVariable UUID studentId) {
        StudentDashboardDTO dashboard = studentService.getStudentDashboard(studentId);
        return ResponseEntity.ok(new ApiResponse(true, "Student dashboard data loaded", dashboard));
    }
}
