package com.brightway.brightway_dropout.dto.parent.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentDashboardDTO {
    private int totalChildren;
    private List<ParentChildSummaryDTO> children;
}
