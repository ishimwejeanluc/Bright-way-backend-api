package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.principal.response.PrincipalDashboardOverviewResponseDTO;
import com.brightway.brightway_dropout.dto.principal.response.PrincipalStudentOverviewResponseDTO;
import java.util.UUID;

public interface IPrincipalService {
    PrincipalDashboardOverviewResponseDTO getDashboardOverview(UUID schoolId);
    PrincipalStudentOverviewResponseDTO getStudentOverview(UUID schoolId);
}
