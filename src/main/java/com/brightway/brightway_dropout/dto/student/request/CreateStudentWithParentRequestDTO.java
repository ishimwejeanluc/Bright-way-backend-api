package com.brightway.brightway_dropout.dto.student.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateStudentWithParentRequestDTO {
    // Parent info
    private String parentName;
    private String parentEmail;
    private String parentPassword;
    private String parentPhone;
    private String parentOccupation;

    // Student info
    private String studentName;
    private String studentEmail;
    private String studentPassword;
    private String studentPhone;
    private String dateOfBirth;
    private String gender;
    private int enrollmentYear;
    private UUID schoolId;
}
