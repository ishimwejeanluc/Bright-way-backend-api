package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Parent extends AbstractBaseUtility {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String occupation;

    @OneToMany(mappedBy = "parent")
    private List<Student> students;


}
