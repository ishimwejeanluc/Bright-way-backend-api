package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.report.response.SchoolReportResponseDTO;
import com.brightway.brightway_dropout.enumeration.EReportType;
import com.brightway.brightway_dropout.service.IReportService;
import com.brightway.brightway_dropout.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final IReportService reportService;
    
    /**
     * Get comprehensive school report for principals
     * Supports three report types:
     * - OVERALL (default): School-wide summary with all statistics
     * - ATTENDANCE: Per-course attendance grids
     * - GRADES: Per-course student grades
     * 
     * @param schoolId The UUID of the school
     * @param type The type of report (OVERALL, ATTENDANCE, or GRADES)
     * @return School report based on type
     */
    @GetMapping("/principal/{schoolId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getPrincipalReport(
            @PathVariable UUID schoolId,
            @RequestParam(required = false, defaultValue = "OVERALL") String type) {
        
        EReportType reportType;
        try {
            reportType = EReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            reportType = EReportType.OVERALL;
        }
        
        Object response = reportService.getPrincipalReport(schoolId, reportType);
        
        String message = reportType == EReportType.OVERALL 
            ? "School overall report retrieved successfully"
            : "School " + reportType.name().toLowerCase() + " report retrieved successfully";
            
        return new ResponseEntity<>(
                new ApiResponse(true, message, response),
                HttpStatus.OK
        );
    }

    
    @GetMapping("/government")
    @PreAuthorize("hasRole('GOVERNMENT')")
    public ResponseEntity<ApiResponse> getGovernmentReport(
            @RequestParam(required = false, defaultValue = "OVERALL") String type) {
        EReportType reportType;
        try {
            reportType = EReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            reportType = EReportType.OVERALL;
        }
        Object response = reportService.getGovernmentReport(reportType);
        String message = reportType == EReportType.OVERALL
                ? "Government overall report retrieved successfully"
                : "Government " + reportType.name().toLowerCase() + " report retrieved successfully";
        return new ResponseEntity<>(
                new ApiResponse(true, message, response),
                HttpStatus.OK
        );
    }
}
