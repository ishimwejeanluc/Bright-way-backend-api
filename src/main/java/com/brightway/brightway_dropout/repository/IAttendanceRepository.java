package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAttendanceRepository extends JpaRepository<Attendance, UUID> {
    
    Optional<Attendance> findByStudentIdAndDate(UUID studentId, LocalDate date);
    
    List<Attendance> findByDateAndStudentIdIn(LocalDate date, List<UUID> studentIds);
    
    @Query("SELECT a FROM Attendance a WHERE a.date = :date AND a.student.id IN :studentIds")
    List<Attendance> findByDateAndStudentIds(@Param("date") LocalDate date, @Param("studentIds") List<UUID> studentIds);
    
    @Query("SELECT a FROM Attendance a JOIN Enrollment e ON a.student.id = e.student.id WHERE a.date = :date AND e.course.id = :courseId")
    List<Attendance> findByDateAndCourseId(@Param("date") LocalDate date, @Param("courseId") UUID courseId);
}