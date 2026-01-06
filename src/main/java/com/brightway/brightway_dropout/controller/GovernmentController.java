package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.government.response.GovDashboardOverviewResponseDTO;
import com.brightway.brightway_dropout.service.IGovernmentService;
import com.brightway.brightway_dropout.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/government")
@RequiredArgsConstructor
public class GovernmentController {
    
    private final IGovernmentService governmentService;

    @GetMapping("/dashboard-overview")
    @PreAuthorize("hasRole('GOVERNMENT')")
    public ResponseEntity<ApiResponse> getDashboardOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID schoolId) {
        
        GovDashboardOverviewResponseDTO response = governmentService.getDashboardOverview(startDate, endDate, schoolId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Government dashboard overview retrieved successfully", response),
            HttpStatus.OK
        );
    }

    @GetMapping("/schools-overview")
    @PreAuthorize("hasRole('GOVERNMENT')")
    public ResponseEntity<ApiResponse> getSchoolsOverview() {
        var response = governmentService.getSchoolsOverview();
        return new ResponseEntity<>(
            new ApiResponse(true, "Schools overview retrieved successfully", response),
            HttpStatus.OK
        );
    }
}
