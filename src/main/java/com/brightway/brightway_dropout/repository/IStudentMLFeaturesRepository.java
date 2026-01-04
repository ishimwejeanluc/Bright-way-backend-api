package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.StudentMLFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IStudentMLFeaturesRepository extends JpaRepository<StudentMLFeatures, UUID> {
    
    Optional<StudentMLFeatures> findTopByStudent_IdOrderByCalculatedAtDesc(UUID studentId);
}
