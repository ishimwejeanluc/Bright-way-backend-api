package com.brightway.brightway_dropout.service;



import com.brightway.brightway_dropout.dto.behaviorIncident.request.RegisterBehaviorIncidentDTO;
import com.brightway.brightway_dropout.dto.behaviorIncident.response.*;
import com.brightway.brightway_dropout.model.BehaviorIncident;
import com.brightway.brightway_dropout.enumeration.ESeverityLevel;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.BehaviorIncident;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.model.Teacher;
import com.brightway.brightway_dropout.repository.IBehaviorIncidentRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import com.brightway.brightway_dropout.repository.ITeacherRepository;
import com.brightway.brightway_dropout.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BehaviorIncidentServiceImpl implements IBehaviorIncidentService {
    private final IBehaviorIncidentRepository behaviorIncidentRepository;
    private final IStudentRepository studentRepository;
    private final ITeacherRepository teacherRepository;
    private final JwtUtil jwtUtil;
    @Override
    public RegisterBehaviorIncidentResponseDTO saveIncident(RegisterBehaviorIncidentDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

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

    @Override
    public BehaviorIncidentStatsResponseDTO getBehaviorIncidentStats(UUID userId) {
        // Get teacher ID from user ID using the helper method
        UUID teacherId = getTeacherId(userId);
        
        // Use repository query with joins to get teacher's student incidents directly
        List<BehaviorIncident> teacherIncidents = behaviorIncidentRepository.findByTeacherId(teacherId);
        
        int totalReports = teacherIncidents.size();
        
        int totalMajorIncidents = (int) teacherIncidents.stream()
                .filter(incident -> incident.getSeverity() == ESeverityLevel.HIGH || 
                                  incident.getSeverity() == ESeverityLevel.CRITICAL)
                .count();
                
        int totalMinorIncidents = (int) teacherIncidents.stream()
                .filter(incident -> incident.getSeverity() == ESeverityLevel.LOW || 
                                  incident.getSeverity() == ESeverityLevel.MEDIUM)
                .count();
        
        List<BehaviorIncidentReportDTO> reports = teacherIncidents.stream()
                .map(incident -> new BehaviorIncidentReportDTO(
                        incident.getStudent().getUser().getName(),
                        incident.getSeverity(),
                        incident.getType(),
                        incident.getNotes()
                ))
                .collect(Collectors.toList());
        
        return new BehaviorIncidentStatsResponseDTO(
                totalReports,
                totalMajorIncidents,
                totalMinorIncidents,
                reports
        );
    }
    public UUID getTeacherId(UUID userId){
    Teacher teacher = teacherRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        return teacher.getId();
    }

    public StBehaviorIncidentOverviewDTO getStudentBehaviorIncidentOverview(UUID studentId) {
        List<BehaviorIncident> incidents = behaviorIncidentRepository.findByStudentId(studentId);
        int totalIncidents = incidents.size();
        int totalMajorIncidents = 0;
        int totalLowIncidents = 0;
        List<StBehaviorIncidentDTO> incidentDTOs = new ArrayList<>();
        for (BehaviorIncident i : incidents) {
            if (i.getSeverity() == ESeverityLevel.HIGH || i.getSeverity() == ESeverityLevel.CRITICAL) {
                totalMajorIncidents++;
            } else if (i.getSeverity() == ESeverityLevel.LOW || i.getSeverity() == ESeverityLevel.MEDIUM) {
                totalLowIncidents++;
            }
            incidentDTOs.add(new StBehaviorIncidentDTO(
                i.getNotes(),
                i.getType() != null ? i.getType().name() : null,
                i.getSeverity() != null ? i.getSeverity().name() : null
            ));
        }
        return new StBehaviorIncidentOverviewDTO(totalIncidents, totalMajorIncidents, totalLowIncidents, incidentDTOs);
    }
}
