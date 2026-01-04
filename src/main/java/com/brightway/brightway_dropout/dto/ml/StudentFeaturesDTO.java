package com.brightway.brightway_dropout.dto.ml;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeaturesDTO {
    
    // Attendance features (last 2 weeks)
    @NotNull(message = "Attendance rate is required")
    @DecimalMin(value = "0.0", message = "Attendance rate must be at least 0")
    @DecimalMax(value = "100.0", message = "Attendance rate must be at most 100")
    private Double attendanceRate;
    
    @NotNull(message = "Days absent is required")
    @Min(value = 0, message = "Days absent must be at least 0")
    private Integer daysAbsent;
    
    @NotNull(message = "Consecutive absences is required")
    @Min(value = 0, message = "Consecutive absences must be at least 0")
    private Integer consecutiveAbsences;
    
    // Grade features (current semester)
    @NotNull(message = "Average marks is required")
    @DecimalMin(value = "0.0", message = "Average marks must be at least 0")
    @DecimalMax(value = "100.0", message = "Average marks must be at most 100")
    private Double averageMarks;
    
    @NotNull(message = "Failing courses count is required")
    @Min(value = 0, message = "Failing courses count must be at least 0")
    private Integer failingCoursesCount;
    
    @NotNull(message = "Lowest grade is required")
    @DecimalMin(value = "0.0", message = "Lowest grade must be at least 0")
    @DecimalMax(value = "100.0", message = "Lowest grade must be at most 100")
    private Double lowestGrade;
    
    // Behavior features (last 2 weeks)
    @NotNull(message = "Incident count is required")
    @Min(value = 0, message = "Incident count must be at least 0")
    private Integer incidentCount;
    
    @NotNull(message = "Severity score is required")
    @Min(value = 0, message = "Severity score must be at least 0")
    @Max(value = 100, message = "Severity score must be at most 100")
    private Integer severityScore;
    
    @NotNull(message = "Days since last incident is required")
    @Min(value = 0, message = "Days since last incident must be at least 0")
    private Integer daysSinceLastIncident;
    
    // Demographics
    @NotNull(message = "Weeks enrolled is required")
    @Min(value = 0, message = "Weeks enrolled must be at least 0")
    private Integer weeksEnrolled;
    
    @NotNull(message = "Age is required")
    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 25, message = "Age must be at most 25")
    private Integer age;
    
    @NotNull(message = "Gender encoded is required")
    @Min(value = 0, message = "Gender must be 0 (Female) or 1 (Male)")
    @Max(value = 1, message = "Gender must be 0 (Female) or 1 (Male)")
    private Integer genderEncoded; // 0=FEMALE, 1=MALE
}
