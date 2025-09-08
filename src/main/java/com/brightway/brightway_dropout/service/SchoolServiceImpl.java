package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.requestdtos.CreateSchoolDTO;
import com.brightway.brightway_dropout.dto.responsedtos.CreateSchoolResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.responsedtos.SchoolResponseDTO;
import com.brightway.brightway_dropout.exception.SchoolAlreadyExistsException;
import com.brightway.brightway_dropout.exception.SchoolNotFoundException;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import com.brightway.brightway_dropout.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements ISchoolService {
    private final ISchoolRepository schoolRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public CreateSchoolResponseDTO createSchool(CreateSchoolDTO createSchoolDTO) {
        try {
            // Check if school already exists by name
            Optional<School> existingSchool = schoolRepository.findByName(createSchoolDTO.getName());
            if (existingSchool.isPresent()) {
                throw new SchoolAlreadyExistsException("School with name " + createSchoolDTO.getName() + " already exists");
            }

            // Create new school
            School newSchool = new School();
            newSchool.setName(createSchoolDTO.getName());
            newSchool.setRegion(createSchoolDTO.getRegion());
            newSchool.setAddress(createSchoolDTO.getAddress());
            newSchool.setType(createSchoolDTO.getType());
            
            // Set audit fields using simple JWT extraction
            Long currentUserId = jwtUtil.getCurrentUserId();
            if (currentUserId != null) {
                newSchool.setCreatedBy(currentUserId.toString());
                newSchool.setModifiedBy(currentUserId.toString());
            }

            // Save school
            School savedSchool = schoolRepository.save(newSchool);

            // Return response DTO
            return new CreateSchoolResponseDTO(
                    savedSchool.getId(),
                    savedSchool.getName()

            );

        } catch (SchoolAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred during school creation", e);
        }
    }

    @Override
    public SchoolResponseDTO getSchoolById(UUID id) {
        try {
            School school = schoolRepository.findById(id)
                    .orElseThrow(() -> new SchoolNotFoundException("School with ID " + id + " not found"));

            return new SchoolResponseDTO(
                    school.getId(),
                    school.getName(),
                    school.getRegion(),
                    school.getAddress(),
                    school.getType()
            );

        } catch (SchoolNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred while fetching school", e);
        }
    }

    @Override
    public List<SchoolResponseDTO> getAllSchools() {
        try {
            List<School> schools = schoolRepository.findAll();
            
            return schools.stream()
                    .map(school -> new SchoolResponseDTO(
                            school.getId(),
                            school.getName(),
                            school.getRegion(),
                            school.getAddress(),
                            school.getType()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred while fetching schools", e);
        }
    }

    @Override
    @Transactional
    public DeleteResponseDTO deleteSchool(UUID id) {
        try {
            School school = schoolRepository.findById(id)
                    .orElseThrow(() -> new SchoolNotFoundException("School with ID " + id + " not found"));

            schoolRepository.delete(school);
            
            return new DeleteResponseDTO("School deleted successfully");

        } catch (SchoolNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Internal server error occurred while deleting school", e);
        }
    }
}
