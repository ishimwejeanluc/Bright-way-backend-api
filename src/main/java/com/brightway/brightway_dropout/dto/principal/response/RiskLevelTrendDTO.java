package com.brightway.brightway_dropout.dto.principal.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskLevelTrendDTO {
        private String date;
        private int low;
        private int medium;
        private int high;
        private int critical;
    }

