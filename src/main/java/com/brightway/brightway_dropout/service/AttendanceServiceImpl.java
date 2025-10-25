package com.brightway.brightway_dropout.service;

import com.brightway.brightway_dropout.dto.attendance.request.AttendanceRequestDTO;
import com.brightway.brightway_dropout.dto.attendance.response.AttendanceResponseDTO;
import com.brightway.brightway_dropout.enumeration.EAttendanceStatus;
import com.brightway.brightway_dropout.exception.ResourceAlreadyExistsException;
import com.brightway.brightway_dropout.exception.ResourceNotFoundException;
import com.brightway.brightway_dropout.model.Attendance;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.repository.IAttendanceRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService {
    
    private final IAttendanceRepository attendanceRepository;
    private final IStudentRepository studentRepository;
    
    @Override
    @Transactional
    public AttendanceResponseDTO saveBulkAttendance(AttendanceRequestDTO requestDTO) {
        LocalDate currentDate = LocalDate.now();
        UUID courseId = requestDTO.getCourseId();
        List<UUID> presentStudentIds = requestDTO.getPresentStudentIds();
        List<UUID> absentStudentIds = requestDTO.getAbsentStudentIds();
        
        // Check if attendance has already been taken for this course today
        List<Attendance> existingAttendanceForCourse = attendanceRepository.findByDateAndCourseId(currentDate, courseId);
        if (!existingAttendanceForCourse.isEmpty()) {
            throw new ResourceAlreadyExistsException("Attendance has already been taken for this course today");
        }
        
        // Combine all student IDs
        List<UUID> allStudentIds = new ArrayList<>();
        if (presentStudentIds != null) {
            allStudentIds.addAll(presentStudentIds);
        }
        if (absentStudentIds != null) {
            allStudentIds.addAll(absentStudentIds);
        }
        
        // Validate that students exist
        List<Student> students = studentRepository.findAllById(allStudentIds);
        if (students.size() != allStudentIds.size()) {
            throw new ResourceNotFoundException("One or more students not found");
        }
        
        // Create a map for quick student lookup
        Map<UUID, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));
        
        List<Attendance> attendanceToSave = new ArrayList<>();
        
        // Process present students
        if (presentStudentIds != null) {
            for (UUID studentId : presentStudentIds) {
                Attendance attendance = new Attendance();
                attendance.setStudent(studentMap.get(studentId));
                attendance.setDate(currentDate);
                attendance.setStatus(EAttendanceStatus.PRESENT);
                attendanceToSave.add(attendance);
            }
        }
        
        // Process absent students
        if (absentStudentIds != null) {
            for (UUID studentId : absentStudentIds) {
                Attendance attendance = new Attendance();
                attendance.setStudent(studentMap.get(studentId));
                attendance.setDate(currentDate);
                attendance.setStatus(EAttendanceStatus.ABSENT);
                attendanceToSave.add(attendance);
            }
        }
        
        // Save all attendance records
        attendanceRepository.saveAll(attendanceToSave);
        
        int totalPresent = presentStudentIds != null ? presentStudentIds.size() : 0;
        int totalAbsent = absentStudentIds != null ? absentStudentIds.size() : 0;
        
        return new AttendanceResponseDTO(String.format("Attendance saved successfully for %s - %d present, %d absent", 
                currentDate, totalPresent, totalAbsent));
    }
}