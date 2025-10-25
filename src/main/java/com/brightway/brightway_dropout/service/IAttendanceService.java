package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.attendance.request.AttendanceRequestDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceResponseDTO;

public interface IAttendanceService {
    
    AttendanceResponseDTO saveBulkAttendance(AttendanceRequestDTO requestDTO);
}