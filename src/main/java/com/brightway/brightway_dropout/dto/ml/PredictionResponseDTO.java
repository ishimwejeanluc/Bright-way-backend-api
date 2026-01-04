package com.brightway.brightway_dropout.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponseDTO {
    
    private List<PredictionResultItem> predictions;
    private Integer processedCount;
    private Double processingTimeMs;
}
