package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.CreateCourseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateCourseResponseDTO;

public interface ICourseService {


    CreateCourseResponseDTO createCourse(CreateCourseDTO createCourseDTO);
}