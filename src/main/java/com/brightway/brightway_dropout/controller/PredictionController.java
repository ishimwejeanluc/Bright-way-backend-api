package com.brightway.brightway_dropout.controller;

import com.brightway.brightway_dropout.dto.prediction.response.BatchPredictionResponseDTO;
import com.brightway.brightway_dropout.dto.prediction.response.SinglePredictionResponseDTO;
import com.brightway.brightway_dropout.service.ml.IDropoutPredictionService;
import com.brightway.brightway_dropout.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {
    
    private final IDropoutPredictionService predictionService;
    
    @PostMapping("/run-batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRINCIPAL')")
    public ResponseEntity<ApiResponse> runBatchPredictions() {
        BatchPredictionResponseDTO response = predictionService.runManualPredictions();
        return new ResponseEntity<>(
                new ApiResponse(true, "Batch predictions completed successfully", response),
                HttpStatus.OK
        );
    }
    
    @PostMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRINCIPAL') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse> runPredictionForStudent(@PathVariable UUID studentId) {
        SinglePredictionResponseDTO response = predictionService.runPredictionForStudent(studentId);
        return new ResponseEntity<>(
                new ApiResponse(true, "Prediction completed", response),
                HttpStatus.OK
        );
    }
}
