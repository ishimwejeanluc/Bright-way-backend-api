package com.brightway.brightway_dropout.dto.ml;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequestDTO {
    
    @NotEmpty(message = "Predictions list cannot be empty")
    @Size(min = 1, max = 1000, message = "Batch size must be between 1 and 1000 students")
    @Valid
    private List<PredictionRequestItem> predictions;
}
