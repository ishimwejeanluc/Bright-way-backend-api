package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.school.request.CreateSchoolDTO;
import com.brightway.brightway_dropout.dto.school.response.CreateSchoolResponseDTO;
import com.brightway.brightway_dropout.dto.common.response.DeleteResponseDTO;
import com.brightway.brightway_dropout.dto.school.response.SchoolResponseDTO;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import com.brightway.brightway_dropout.repository.IAuthRepository;
import com.brightway.brightway_dropout.dto.school.request.PrincipalDTO;
import com.brightway.brightway_dropout.model.User;
import com.brightway.brightway_dropout.enumeration.EUserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final IAuthRepository authRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
        @Transactional
        public SchoolResponseDTO updateSchool(UUID id, CreateSchoolDTO updateSchoolDTO) {
            School school = schoolRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("School with ID " + id + " not found"));

            if (!school.getName().equals(updateSchoolDTO.getName())) {
                Optional<School> existingSchool = schoolRepository.findByName(updateSchoolDTO.getName());
                if (existingSchool.isPresent()) {
                    throw new ResourceAlreadyExistsException("School with name " + updateSchoolDTO.getName() + " already exists");
                }
                school.setName(updateSchoolDTO.getName());
            }

            school.setRegion(updateSchoolDTO.getRegion());
            school.setAddress(updateSchoolDTO.getAddress());
            school.setType(updateSchoolDTO.getType());

            UUID currentUserId = jwtUtil.getCurrentUserId();
            if (currentUserId != null) {
                school.setModifiedBy(currentUserId.toString());
            }

            School updatedSchool = schoolRepository.save(school);

            return new SchoolResponseDTO(
                    updatedSchool.getId(),
                    updatedSchool.getName(),
                    updatedSchool.getRegion(),
                    updatedSchool.getAddress(),
                    updatedSchool.getType()
            );
        }
    @Transactional
    public CreateSchoolResponseDTO createSchool(CreateSchoolDTO createSchoolDTO) {
        Optional<School> existingSchool = schoolRepository.findByName(createSchoolDTO.getName());
        if (existingSchool.isPresent()) {
            throw new ResourceAlreadyExistsException("School with name " + createSchoolDTO.getName() + " already exists");
        }


        PrincipalDTO principalDTO = createSchoolDTO.getPrincipal();
        if (authRepository.findByEmail(principalDTO.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Principal with email " + principalDTO.getEmail() + " already exists");
        }

        User principal = new User();
        principal.setName(principalDTO.getName());
        principal.setEmail(principalDTO.getEmail());
        String hashedPassword = passwordEncoder.encode(principalDTO.getPassword());
        principal.setPassword(hashedPassword);
        principal.setPhone(principalDTO.getPhone());
        principal.setRole(EUserRole.PRINCIPAL);
        authRepository.save(principal);

        School newSchool = new School();
        newSchool.setName(createSchoolDTO.getName());
        newSchool.setRegion(createSchoolDTO.getRegion());
        newSchool.setAddress(createSchoolDTO.getAddress());
        newSchool.setType(createSchoolDTO.getType());
        newSchool.setPrincipal(principal);

        UUID currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId != null) {
            newSchool.setCreatedBy(currentUserId.toString());
            newSchool.setModifiedBy(currentUserId.toString());
        }

        School savedSchool = schoolRepository.save(newSchool);

        return new CreateSchoolResponseDTO(
                savedSchool.getId(),
                savedSchool.getName()
        );
    }

    @Override
    public SchoolResponseDTO getSchoolById(UUID id) {
    School school = schoolRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("School with ID " + id + " not found"));
    return new SchoolResponseDTO(
        school.getId(),
        school.getName(),
        school.getRegion(),
        school.getAddress(),
        school.getType()
    );
    }

    @Override
    public List<SchoolResponseDTO> getAllSchools() {
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
    }

    @Override
    @Transactional
    public DeleteResponseDTO deleteSchool(UUID id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School with ID " + id + " not found"));
        schoolRepository.delete(school);
        return new DeleteResponseDTO("School deleted successfully");
    }
}
