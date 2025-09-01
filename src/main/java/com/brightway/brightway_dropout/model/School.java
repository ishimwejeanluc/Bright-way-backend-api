package com.brightway.brightway_dropout.model;


import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class School extends AbstractBaseUtility {
    private String name;
    private String region;
    private String address;
    private String type;

    @OneToMany(mappedBy = "school")
    private List<Student> students;

    @OneToMany(mappedBy = "school")
    private List<Teacher> teachers;


}
