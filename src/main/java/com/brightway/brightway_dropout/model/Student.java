package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.enumeration.EGender;
import com.brightway.brightway_dropout.enumeration.EStudentStatus;
import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Student extends AbstractBaseUtility {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private EGender gender;
    private int enrollmentYear;
    @Enumerated(EnumType.STRING)
    private EStudentStatus status;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendanceRecords;

    @OneToMany(mappedBy = "student")
    private List<BehaviorIncident> behaviorIncidents;

    @OneToMany(mappedBy = "student")
    private List<DropoutPrediction> dropoutPredictions;


}
