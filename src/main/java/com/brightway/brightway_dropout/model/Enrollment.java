package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.enumeration.ESemester;
import com.brightway.brightway_dropout.util.AbstractBaseUtility;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Enrollment extends AbstractBaseUtility {
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String academicYear;
    @Enumerated(EnumType.STRING)
    private ESemester semester;

    @OneToMany(mappedBy = "enrollment")
    private List<Grade> grades;


}
