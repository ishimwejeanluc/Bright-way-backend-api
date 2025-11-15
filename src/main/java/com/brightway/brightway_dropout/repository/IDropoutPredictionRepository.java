package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.DropoutPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface IDropoutPredictionRepository extends JpaRepository<DropoutPrediction, UUID> {
    // Get the latest prediction for a student
    @Query("SELECT dp FROM DropoutPrediction dp WHERE dp.student.id = :studentId ORDER BY dp.predictedAt DESC LIMIT 1")
    DropoutPrediction findTopByStudentIdOrderByPredictedAtDesc(@Param("studentId") UUID studentId);
}
