package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.DropoutPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface IDropoutPredictionRepository extends JpaRepository<DropoutPrediction, UUID> {
    
    Optional<DropoutPrediction> findTopByStudentIdOrderByPredictedAtDesc(UUID studentId);
}
