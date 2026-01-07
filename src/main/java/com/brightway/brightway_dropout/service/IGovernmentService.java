package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.government.response.*;

import java.time.LocalDate;
import java.util.UUID;

public interface IGovernmentService {
    GovDashboardOverviewResponseDTO getDashboardOverview(LocalDate startDate, LocalDate endDate, UUID schoolId);
    GovSchoolsOverviewResponseDTO getSchoolsOverview();
    GovSchoolProfileOverviewDTO getSchoolProfileOverview(UUID schoolId);
    GovStudentOverviewResponseDTO getStudentOverview();
    GovStudentDetailsResponseDTO getStudentDetails(UUID schoolId);
    GovTeacherOverviewResponseDTO getTeacherOverview();
    GovTeacherDetailsResponseDTO getTeacherDetails(UUID schoolId);
}
