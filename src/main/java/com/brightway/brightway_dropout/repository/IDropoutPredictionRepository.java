package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.DropoutPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IDropoutPredictionRepository extends JpaRepository<DropoutPrediction, UUID> {
        @Query("SELECT d FROM DropoutPrediction d WHERE d.student.school.id = :schoolId")
        java.util.List<DropoutPrediction> findAllBySchoolId(@Param("schoolId") UUID schoolId);

         @Query("SELECT d FROM DropoutPrediction d WHERE d.student.id IN :studentIds")
        java.util.List<DropoutPrediction> findLatestByStudentIds(@Param("studentIds") java.util.List<UUID> studentIds);
    
    Optional<DropoutPrediction> findTopByStudentIdOrderByPredictedAtDesc(UUID studentId);
}
