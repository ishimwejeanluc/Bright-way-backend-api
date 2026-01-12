package com.brightway.brightway_dropout.dto.principal.response;

import com.brightway.brightway_dropout.dto.student.response.StCourseMarkDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.StBehaviorIncidentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrincipalStudentProfileDTO {
    // Basic Info
    private UUID studentId;
    private String name;
    private String studentCode;
    private String schoolName;
    
    // Parent Info
    private String parentName;
    private String parentPhone;
    private String parentEmail;
    private String parentOccupation;
    
    // Risk Assessment
    private String riskLevel;
    private Double dropoutProbability;
    
    // Performance Timeline
    private Integer avgAttendancePercent;
    private Double academicScore;
    private Integer engagementPercent;
    
    // Current Grades (reusing existing DTO from student package)
    private List<StCourseMarkDTO> currentGrades;
    
    // Intervention Log (reusing existing DTO from behaviorIncident package)
    private List<StBehaviorIncidentDTO> interventionLog;
}
