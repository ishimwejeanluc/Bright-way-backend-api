package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Teacher extends AbstractBaseUtility {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    private String specialization;

    @OneToMany(mappedBy = "teacher")
    private List<Course> courses;

}
