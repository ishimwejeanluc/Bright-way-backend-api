package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.enumeration.EIncidentType;
import com.brightway.brightway_dropout.enumeration.ESeverityLevel;
import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class BehaviorIncident extends AbstractBaseUtility {
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

   
    @Enumerated(EnumType.STRING)
    private EIncidentType type;

    @Enumerated(EnumType.STRING)
    private ESeverityLevel severity;
    private String notes;

}
