package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.school.request.CreateSchoolDTO;
import com.brightway.brightway_dropout.dto.school.response.CreateSchoolResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.school.response.SchoolResponseDTO;
import com.brightway.brightway_dropout.service.ISchoolService;
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
    private final ISchoolService schoolService;

    @PutMapping(value = "/{id}" )
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> updateSchool(@PathVariable UUID id, @Valid @RequestBody CreateSchoolDTO updateSchoolDTO) {
    SchoolResponseDTO response = schoolService.updateSchool(id, updateSchoolDTO);
    return new ResponseEntity<>(
        new ApiResponse(true,
            "School updated successfully",
            response),
        HttpStatus.OK
    );
    }
    

    @PostMapping(value = "/create")
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

    @GetMapping(value = "/{id}" )
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getSchoolById(@PathVariable UUID id) {
        SchoolResponseDTO response = schoolService.getSchoolById(id);
        return new ResponseEntity<>(
                new ApiResponse(true, 
                "School retrieved successfully",
                 response),
                HttpStatus.OK
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> getAllSchools() {
        List<SchoolResponseDTO> response = schoolService.getAllSchools();
        return new ResponseEntity<>(
                new ApiResponse(true, 
                "Schools retrieved successfully",
                 response),
                HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/{id}" )
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
