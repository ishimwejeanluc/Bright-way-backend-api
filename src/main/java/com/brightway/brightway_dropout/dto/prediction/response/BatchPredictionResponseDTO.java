package com.brightway.brightway_dropout.dto.prediction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchPredictionResponseDTO {
    
    private Integer totalStudents;
    private List<PredictionItemResponseDTO> predictions;
}
