package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.attendance.request.AttendanceRequestDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceResponseDTO;
import com.brightway.brightway_dropout.service.IAttendanceService;
import com.brightway.brightway_dropout.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final IAttendanceService attendanceService;
    
    @PostMapping(value = "/bulk-save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('TEACHER') or hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> saveBulkAttendance(@Valid @RequestBody AttendanceRequestDTO requestDTO) {
        AttendanceResponseDTO response = attendanceService.saveBulkAttendance(requestDTO);
        return new ResponseEntity<>(
            new ApiResponse(true, "Attendance saved successfully", response),
            HttpStatus.CREATED
        );
    }
}
