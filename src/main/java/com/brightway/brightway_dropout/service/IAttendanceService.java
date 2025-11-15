package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.attendance.request.AttendanceRequestDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceResponseDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.student.response.StAttendanceOverviewDTO;

import java.util.UUID;

public interface IAttendanceService {
    
    AttendanceResponseDTO saveBulkAttendance(AttendanceRequestDTO requestDTO);
    
    AttendanceOverviewResponseDTO getAttendanceOverview(UUID schoolId);
    
    StAttendanceOverviewDTO getStudentAttendanceOverview(UUID studentId);
}

    