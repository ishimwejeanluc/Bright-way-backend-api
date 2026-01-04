package com.brightway.brightway_dropout.service.ml;

import com.brightway.brightway_dropout.dto.ml.PredictionRequestDTO;
import com.brightway.brightway_dropout.dto.ml.PredictionResponseDTO;

public interface IMLServiceClient {
    
    /**
     * Send batch prediction request to ML service
     * @param request PredictionRequestDTO
     * @return PredictionResponseDTO
     */
    PredictionResponseDTO predictBatch(PredictionRequestDTO request);
    
    /**
     * Check if ML service is healthy and available
     * @return true if healthy, false otherwise
     */
    boolean checkHealth();
}
