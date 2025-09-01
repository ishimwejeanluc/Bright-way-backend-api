package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.enumeration.EGradeType;
import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Grade extends AbstractBaseUtility {
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    private String name;
    private float marks;
    @Enumerated(EnumType.STRING)
    private EGradeType gradeType;


}
