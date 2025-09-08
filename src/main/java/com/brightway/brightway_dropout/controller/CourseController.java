package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.requestdtos.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateCourseResponseDTO;
import com.brightway.brightway_dropout.service.CourseServiceImpl;
import com.brightway.brightway_dropout.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseServiceImpl courseService;

    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createCourse(@Valid @RequestBody CreateCourseDTO createCourseDTO) {
        CreateCourseResponseDTO response = courseService.createCourse(createCourseDTO);
        return new ResponseEntity<>((new ApiResponse(
                true,
                "course created successfully",
                response)
                ),HttpStatus.CREATED
        );
    }
}
