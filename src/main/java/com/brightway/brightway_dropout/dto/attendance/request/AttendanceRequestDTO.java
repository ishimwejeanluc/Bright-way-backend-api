package com.brightway.brightway_dropout.dto.attendance.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequestDTO {
    
    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotEmpty(message = "At least one student must be present or absent")
    private List<UUID> presentStudentIds;
    
    private List<UUID> absentStudentIds;
}