package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.report.response.GovernmentOverallReportDTO;
import com.brightway.brightway_dropout.dto.report.response.GovernmentReportResponseDTO;
import com.brightway.brightway_dropout.dto.report.response.SchoolOverallReportDTO;
import com.brightway.brightway_dropout.dto.report.response.SchoolReportResponseDTO;
import com.brightway.brightway_dropout.enumeration.EReportType;

import java.util.UUID;

public interface IReportService {
    Object getPrincipalReport(UUID schoolId, EReportType reportType);
    SchoolOverallReportDTO getSchoolOverallReport(UUID schoolId);
    SchoolReportResponseDTO getSchoolDetailedReport(UUID schoolId, EReportType reportType);

    // Government-level reports
    Object getGovernmentReport(EReportType reportType);
    GovernmentOverallReportDTO getGovernmentOverallReport();
    Object getGovernmentDetailedReport(EReportType reportType);
}
