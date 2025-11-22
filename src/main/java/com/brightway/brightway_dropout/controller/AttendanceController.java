package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.attendance.request.AttendanceRequestDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceResponseDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceOverviewResponseDTO;
import com.brightway.brightway_dropout.service.IAttendanceService;
import com.brightway.brightway_dropout.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final IAttendanceService attendanceService;
    
    @PostMapping(value = "/bulk-save")
    @PreAuthorize("hasRole('TEACHER') or hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> saveBulkAttendance(@Valid @RequestBody AttendanceRequestDTO requestDTO) {
        AttendanceResponseDTO response = attendanceService.saveBulkAttendance(requestDTO);
        return new ResponseEntity<>(
            new ApiResponse(true, "Attendance saved successfully", response),
            HttpStatus.CREATED
        );
    }

    @GetMapping(value = "/stats-overview/{schoolId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> getAttendanceOverview(@PathVariable UUID schoolId) {
        AttendanceOverviewResponseDTO response = attendanceService.getAttendanceOverview(schoolId);
        return new ResponseEntity<>(
            new ApiResponse(true, "Attendance overview retrieved successfully", response),
            HttpStatus.OK
        );
    }
}
