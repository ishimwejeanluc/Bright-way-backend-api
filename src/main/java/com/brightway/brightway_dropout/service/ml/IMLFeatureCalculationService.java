package com.brightway.brightway_dropout.service.ml;

import com.brightway.brightway_dropout.dto.ml.StudentFeaturesDTO;
import com.brightway.brightway_dropout.model.Student;

import java.util.List;

public interface IMLFeatureCalculationService {
    
    /**
     * Calculate ML features for a single student
     * @param student Student entity
     * @return StudentFeaturesDTO with calculated features
     */
    StudentFeaturesDTO calculateFeatures(Student student);
    
    /**
     * Calculate ML features for multiple students
     * @param students List of student entities
     * @return List of StudentFeaturesDTO
     */
    List<StudentFeaturesDTO> calculateFeaturesForAll(List<Student> students);
}
