package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.government.response.GovDashboardOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.government.response.GovSchoolsOverviewResponseDTO;

import java.time.LocalDate;
import java.util.UUID;

public interface IGovernmentService {
    GovDashboardOverviewResponseDTO getDashboardOverview(LocalDate startDate, LocalDate endDate, UUID schoolId);
    GovSchoolsOverviewResponseDTO getSchoolsOverview();
}
