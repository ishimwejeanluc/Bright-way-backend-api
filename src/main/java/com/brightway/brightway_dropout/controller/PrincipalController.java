package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.principal.response.PrincipalDashboardOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.principal.response.PrincipalStudentOverviewResponseDTO;
import com.brightway.brightway_dropout.service.IPrincipalService;
import com.brightway.brightway_dropout.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/principal")
@RequiredArgsConstructor
public class PrincipalController {
    private final IPrincipalService dashboardService;

    @GetMapping("/dashboard-overview/{schoolId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getDashboardOverview(@PathVariable UUID schoolId) {
        PrincipalDashboardOverviewResponseDTO response = dashboardService.getDashboardOverview(schoolId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Dashboard overview retrieved successfully", response),
            HttpStatus.OK
        );
    }

    @GetMapping("/student-overview/{schoolId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getStudentOverview(@PathVariable UUID schoolId) {
        PrincipalStudentOverviewResponseDTO response = dashboardService.getStudentOverview(schoolId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Student overview retrieved successfully", response),
            HttpStatus.OK
        );
    }
}


