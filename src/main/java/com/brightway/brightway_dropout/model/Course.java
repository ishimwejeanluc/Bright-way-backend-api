package com.brightway.brightway_dropout.model;


import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Entity
public class Course extends AbstractBaseUtility {
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;


}
