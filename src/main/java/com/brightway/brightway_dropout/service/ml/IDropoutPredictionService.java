package com.brightway.brightway_dropout.service.ml;

import com.brightway.brightway_dropout.dto.ml.PredictionResponseDTO;
import com.brightway.brightway_dropout.dto.prediction.response.BatchPredictionResponseDTO;
import com.brightway.brightway_dropout.dto.prediction.response.SinglePredictionResponseDTO;
import com.brightway.brightway_dropout.model.Student;

import java.util.List;
import java.util.UUID;

public interface IDropoutPredictionService {
    
    /**
     * Run scheduled batch predictions for all active students (called by @Scheduled)
     */
    void runScheduledPredictions();
    
    /**
     * Run manual batch predictions for all active students (called by admin via API)
     * @return BatchPredictionResponseDTO for frontend
     */
    BatchPredictionResponseDTO runManualPredictions();
    
    /**
     * Run prediction for single student
     * @param studentId Student UUID
     * @return SinglePredictionResponseDTO for frontend
     */
    SinglePredictionResponseDTO runPredictionForStudent(UUID studentId);
    
    /**
     * Run predictions for specific list of students
     * @param students List of students
     * @return PredictionResponseDTO
     */
    PredictionResponseDTO runPredictionsForStudents(List<Student> students);
}
