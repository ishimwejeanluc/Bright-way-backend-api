package com.brightway.brightway_dropout.repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.brightway.brightway_dropout.model.Attendance;

public interface IAttendanceRepository extends JpaRepository<Attendance, UUID> {
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.school.id = :schoolId AND a.date = :date AND a.status = 'PRESENT'")
    int countPresentBySchoolIdAndDate(@Param("schoolId") UUID schoolId, @Param("date") LocalDate date);
    // Find the most missed class for a student (by absence count)
    @Query(value = """
        SELECT c.name, COUNT(*) AS total_missed
        FROM attendance a
        JOIN enrollment e ON a.student_id = e.student_id
        JOIN course c ON e.course_id = c.id
        WHERE a.student_id = :studentId AND a.status = 'ABSENT'
        GROUP BY c.name
        ORDER BY total_missed DESC
        LIMIT 1
        """, nativeQuery = true)
    Object[] findMostMissedClassForStudent(@Param("studentId") UUID studentId);
    // Attendance rate for a student in a given period (weekly)
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        WHERE a.student_id = :studentId AND a.date >= :startDate AND a.date <= :endDate
        """, nativeQuery = true)
    Double findAttendanceRateForStudentInPeriod(@Param("studentId") UUID studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Attendance rate for a student in a specific course
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        JOIN enrollment e ON a.student_id = e.student_id
        WHERE a.student_id = :studentId AND e.course_id = :courseId
        """, nativeQuery = true)
    Double findAttendanceRateForStudentInCourse(@Param("studentId") UUID studentId, @Param("courseId") UUID courseId);
    
    Optional<Attendance> findByStudentIdAndDate(UUID studentId, LocalDate date);
    
    List<Attendance> findByDateAndStudentIdIn(LocalDate date, List<UUID> studentIds);
    
    @Query("SELECT a FROM Attendance a WHERE a.date = :date AND a.student.id IN :studentIds")
    List<Attendance> findByDateAndStudentIds(@Param("date") LocalDate date, @Param("studentIds") List<UUID> studentIds);
    
    @Query("SELECT a FROM Attendance a JOIN Enrollment e ON a.student.id = e.student.id WHERE a.date = :date AND e.course.id = :courseId")
    List<Attendance> findByDateAndCourseId(@Param("date") LocalDate date, @Param("courseId") UUID courseId);
    
    // Simple attendance KPIs - just present and absent counts for today filtered by school
    @Query(value = """
        SELECT 
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END),
            COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE a.date = :date AND s.school_id = :schoolId
        """, nativeQuery = true)
    Object[] findAttendanceKPIs(@Param("date") LocalDate date, @Param("schoolId") UUID schoolId);
    
    @Query(value = """
        SELECT 
            TO_CHAR(a.date, 'Dy'),
            a.date,
            ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE a.date >= :startDate AND a.date <= :endDate AND s.school_id = :schoolId
        GROUP BY a.date
        ORDER BY a.date
        """, nativeQuery = true)
    List<Object[]> findDailyAttendanceStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("schoolId") UUID schoolId);
    
    // Get total absences for the week filtered by school
    @Query(value = """
        SELECT COUNT(*)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE a.status = 'ABSENT' AND a.date >= :startDate AND a.date <= :endDate AND s.school_id = :schoolId
        """, nativeQuery = true)
    Integer findTotalAbsencesForPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("schoolId") UUID schoolId);
    
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE a.date >= :startDate AND a.date <= :endDate AND s.school_id = :schoolId
        """, nativeQuery = true)
    Double findAttendanceRateForPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("schoolId") UUID schoolId);

    // Attendance rate for a specific student
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        WHERE a.student_id = :studentId
        """, nativeQuery = true)
    Double findAttendanceRateForStudent(@Param("studentId") UUID studentId);

    // Attendance overview by day for a specific student
    @Query(value = """
        SELECT a.date, ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        WHERE a.student_id = :studentId
        GROUP BY a.date
        ORDER BY a.date
        """, nativeQuery = true)
    List<Object[]> findAttendanceOverviewByDayForStudent(@Param("studentId") UUID studentId);

    List<Attendance> findByStudent_IdAndDateAfter(UUID studentId, LocalDate date);
    List<Attendance> findByStudent_IdAndCreatedAtAfter(UUID studentId , LocalDateTime date);
    
    // Government dashboard queries - calculate average attendance across all schools
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE a.date >= :startDate AND a.date <= :endDate
        """, nativeQuery = true)
    Double calculateAverageAttendanceForAllSchools(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    // Calculate average attendance for specific schools
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE a.date >= :startDate AND a.date <= :endDate
        AND s.school_id IN :schoolIds
        """, nativeQuery = true)
    Double calculateAverageAttendanceForSpecificSchools(@Param("startDate") LocalDate startDate, 
                                                         @Param("endDate") LocalDate endDate,
                                                         @Param("schoolIds") List<UUID> schoolIds);
    
    // Calculate attendance for a specific school in date range
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE a.date >= :startDate AND a.date <= :endDate AND s.school_id = :schoolId
        """, nativeQuery = true)
    Double calculateAttendanceForSchool(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("schoolId") UUID schoolId);
    
    // Calculate average attendance for a specific school (all time)
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 100.0) / NULLIF(COUNT(*), 0), 1)
        FROM attendance a
        JOIN student s ON a.student_id = s.id
        WHERE s.school_id = :schoolId
        """, nativeQuery = true)
    Double calculateAverageAttendanceForSchoolAllTime(@Param("schoolId") UUID schoolId);
    
    // Weekly attendance overview for teacher - aggregated across all their courses
    @Query(value = """
        SELECT 
            a.date,
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as present_count,
            COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) as absent_count
        FROM attendance a
        INNER JOIN student s ON a.student_id = s.id
        INNER JOIN enrollment e ON s.id = e.student_id
        INNER JOIN course c ON e.course_id = c.id
        WHERE c.teacher_id = :teacherId
          AND a.date >= :startDate AND a.date <= :endDate
        GROUP BY a.date
        ORDER BY a.date
        """, nativeQuery = true)
    List<Object[]> findWeeklyAttendanceByTeacher(@Param("teacherId") UUID teacherId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);

    // Method to find students with most total absences for a teacher
    @Query(value = """
        SELECT u.name, COUNT(a.id) as totalAbsences
        FROM attendance a
        INNER JOIN student s ON a.student_id = s.id
        INNER JOIN users u ON s.user_id = u.id
        INNER JOIN enrollment e ON s.id = e.student_id
        INNER JOIN course c ON e.course_id = c.id
        WHERE c.teacher_id = :teacherId
        AND a.status = 'ABSENT'
        GROUP BY s.id, u.name
        ORDER BY totalAbsences DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findStudentsWithMostTotalAbsences(@Param("teacherId") UUID teacherId, @Param("limit") int limit);

    // Method to find students with most consecutive absences for a teacher
    @Query(value = """
        WITH consecutive_absences AS (
            SELECT 
                s.id as student_id,
                u.name as student_name,
                a.date,
                a.status,
                ROW_NUMBER() OVER (PARTITION BY s.id ORDER BY a.date) - 
                ROW_NUMBER() OVER (PARTITION BY s.id, a.status ORDER BY a.date) as grp
            FROM attendance a
            INNER JOIN student s ON a.student_id = s.id
            INNER JOIN users u ON s.user_id = u.id
            INNER JOIN enrollment e ON s.id = e.student_id
            INNER JOIN course c ON e.course_id = c.id
            WHERE c.teacher_id = :teacherId
            AND a.status = 'ABSENT'
        ),
        consecutive_counts AS (
            SELECT 
                student_id,
                student_name,
                COUNT(*) as consecutive_absences
            FROM consecutive_absences
            GROUP BY student_id, student_name, grp
        )
        SELECT 
            student_name,
            MAX(consecutive_absences) as max_consecutive
        FROM consecutive_counts
        GROUP BY student_id, student_name
        ORDER BY max_consecutive DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findStudentsWithMostConsecutiveAbsences(@Param("teacherId") UUID teacherId, @Param("limit") int limit);
}