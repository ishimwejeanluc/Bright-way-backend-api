package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.enumeration.EAttendanceStatus;
import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Attendance extends AbstractBaseUtility {
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private EAttendanceStatus status;
    private String remarks;


}
