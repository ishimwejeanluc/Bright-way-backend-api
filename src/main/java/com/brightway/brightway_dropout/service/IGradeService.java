package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.grade.request.RegisterGradeBulkDTO;
import com.brightway.brightway_dropout.dto.grade.response.RegisterGradeBulkResponseDTO;

public interface IGradeService {
    RegisterGradeBulkResponseDTO registerGrades(RegisterGradeBulkDTO dto);
}
