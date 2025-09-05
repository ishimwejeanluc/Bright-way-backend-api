package com.brightway.brightway_dropout.model;

import com.brightway.brightway_dropout.enumeration.EUserRole;
import com.brightway.brightway_dropout.util.AbstractBaseUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Table(name = "users")
@Setter
@Entity
public class User extends AbstractBaseUtility {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private EUserRole role;


}
