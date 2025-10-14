package com.brightway.brightway_dropout.service;



import com.brightway.brightway_dropout.dto.behaviorIncident.request.RegisterBehaviorIncidentDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.RegisterBehaviorIncidentResponseDTO;
import com.brightway.brightway_dropout.enumeration.EIncidentType;
import com.brightway.brightway_dropout.enumeration.ESeverityLevel;
import com.brightway.brightway_dropout.model.BehaviorIncident;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.repository.IBehaviorIncidentRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import com.brightway.brightway_dropout.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BehaviorIncidentServiceImpl implements IBehaviorIncidentService {
    private final IBehaviorIncidentRepository behaviorIncidentRepository;
    private final IStudentRepository studentRepository;
    private final JwtUtil jwtUtil;
    @Override
    public RegisterBehaviorIncidentResponseDTO saveIncident(RegisterBehaviorIncidentDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        UUID currentUser = jwtUtil.getCurrentUserId();

        BehaviorIncident incident = new BehaviorIncident();
        incident.setStudent(student);
        incident.setNotes(dto.getNotes());
        incident.setType(dto.getIncidentType());
        incident.setSeverity(dto.getSeverity());
        incident.setCreatedAt(LocalDateTime.now());
        incident.setCreatedBy(currentUser.toString());
        

        BehaviorIncident saved = behaviorIncidentRepository.save(incident);

        return new RegisterBehaviorIncidentResponseDTO(
                saved.getId(),
                saved.getStudent().getId(),
                "Incident reported for student: " + saved.getStudent().getId()
        );
                                
        
    }
}
