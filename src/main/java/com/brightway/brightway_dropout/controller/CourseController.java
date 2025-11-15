package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.course.request.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CourseStatsResponseDTO;
import com.brightway.brightway_dropout.dto.course.response.CreateCourseResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.service.ICourseService;
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
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> createCourse(@Valid @RequestBody CreateCourseDTO createCourseDTO) {
        CreateCourseResponseDTO response = courseService.createCourse(createCourseDTO);
        return new ResponseEntity<>(
                new ApiResponse(true, "Course created successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getCourseById(@PathVariable UUID id) {
        CourseResponseDTO response = courseService.getCourseById(id);
        return new ResponseEntity<>(
                new ApiResponse(true, "Course retrieved successfully", response),
                HttpStatus.OK
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getAllCourses() {
        List<CourseResponseDTO> response = courseService.getAllCourses();
        return new ResponseEntity<>(
                new ApiResponse(true, "Courses retrieved successfully", response),
                HttpStatus.OK
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> updateCourse(@PathVariable UUID id, @Valid @RequestBody CreateCourseDTO updateCourseDTO) {
        CourseResponseDTO response = courseService.updateCourse(id, updateCourseDTO);
        return new ResponseEntity<>(
                new ApiResponse(true, "Course updated successfully", response),
                HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable UUID id) {
        DeleteResponseDTO response = courseService.deleteCourse(id);
        return new ResponseEntity<>(
                new ApiResponse(true, response.getMessage(), null),
                HttpStatus.OK
        );
    }
    @GetMapping(value = "/stats/{schoolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getCourseStatsBySchool(@PathVariable UUID schoolId) {
        CourseStatsResponseDTO response = courseService.getCourseStatsBySchool(schoolId);
        return new ResponseEntity<>(
                new ApiResponse(true, "Course statistics retrieved successfully", response),
                HttpStatus.OK
        );
}
}
