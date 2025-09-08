package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.requestdtos.CreateSchoolDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateSchoolResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.SchoolResponseDTO;
import com.brightway.brightway_dropout.service.SchoolServiceImpl;
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
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolServiceImpl schoolService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createSchool(@Valid @RequestBody CreateSchoolDTO createSchoolDTO) {
        CreateSchoolResponseDTO response = schoolService.createSchool(createSchoolDTO);
        return new ResponseEntity<>(
                new ApiResponse(true, 
                "School created successfully",
                 response),
                HttpStatus.CREATED
        );
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getSchoolById(@PathVariable UUID id) {
        SchoolResponseDTO response = schoolService.getSchoolById(id);
        return new ResponseEntity<>(
                new ApiResponse(true, 
                "School retrieved successfully",
                 response),
                HttpStatus.OK
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getAllSchools() {
        List<SchoolResponseDTO> response = schoolService.getAllSchools();
        return new ResponseEntity<>(
                new ApiResponse(true, 
                "Schools retrieved successfully",
                 response),
                HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteSchool(@PathVariable UUID id) {
        DeleteResponseDTO response = schoolService.deleteSchool(id);
        return new ResponseEntity<>(
                new ApiResponse(true, 
                response.getMessage(),
                 null),
                HttpStatus.OK
        );
    }
}
