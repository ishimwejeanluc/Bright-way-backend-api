package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.DropoutPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IDropoutPredictionRepository extends JpaRepository<DropoutPrediction, UUID> {
        @Query("SELECT d FROM DropoutPrediction d WHERE d.student.school.id = :schoolId")
        java.util.List<DropoutPrediction> findAllBySchoolId(@Param("schoolId") UUID schoolId);

         @Query("SELECT d FROM DropoutPrediction d WHERE d.student.id IN :studentIds")
        java.util.List<DropoutPrediction> findLatestByStudentIds(@Param("studentIds") java.util.List<UUID> studentIds);
    
    Optional<DropoutPrediction> findTopByStudentIdOrderByPredictedAtDesc(UUID studentId);
    
    // Count total at-risk students (HIGH or CRITICAL) across all schools - this is different from dropout
    @Query(value = """
        SELECT COUNT(DISTINCT st.id)
        FROM student st
        LEFT JOIN LATERAL (
            SELECT risk_level
            FROM dropout_predictions
            WHERE student_id = st.id
            ORDER BY created_at DESC
            LIMIT 1
        ) dp ON true
        WHERE dp.risk_level IN ('HIGH', 'CRITICAL')
        """, nativeQuery = true)
    Integer countTotalAtRiskStudents();
    
    // Government dashboard queries - calculate dropout rate based on student status
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN s.status = 'DROPPED' THEN 1 END) * 100.0) / NULLIF(COUNT(s.id), 0), 1)
        FROM student s
        """, nativeQuery = true)
    Double calculateDropoutRateForAllSchools();
    
    // Calculate dropout rate for specific schools based on student status
    @Query(value = """
        SELECT ROUND((COUNT(CASE WHEN s.status = 'DROPPED' THEN 1 END) * 100.0) / NULLIF(COUNT(s.id), 0), 1)
        FROM student s
        WHERE s.school_id IN :schoolIds
        """, nativeQuery = true)
    Double calculateDropoutRateForSpecificSchools(@Param("schoolIds") java.util.List<UUID> schoolIds);
    
    // Calculate risk trends grouped by month for all schools
    @Query(value = """
        SELECT 
            TO_CHAR(latest.created_at, 'Mon') as month,
            COUNT(CASE WHEN latest.risk_level = 'LOW' THEN 1 END) as low_risk,
            COUNT(CASE WHEN latest.risk_level = 'MEDIUM' THEN 1 END) as medium_risk,
            COUNT(CASE WHEN latest.risk_level = 'HIGH' THEN 1 END) as high_risk,
            COUNT(CASE WHEN latest.risk_level = 'CRITICAL' THEN 1 END) as critical_risk
        FROM (
            SELECT DISTINCT ON (dp.student_id)
                dp.student_id,
                dp.created_at,
                dp.risk_level
            FROM dropout_predictions dp
            JOIN student s ON dp.student_id = s.id
            WHERE dp.created_at >= :startDate AND dp.created_at <= :endDate
            ORDER BY dp.student_id, dp.created_at DESC
        ) latest
        GROUP BY TO_CHAR(latest.created_at, 'Mon'), EXTRACT(MONTH FROM latest.created_at)
        ORDER BY EXTRACT(MONTH FROM latest.created_at)
        """, nativeQuery = true)
    java.util.List<Object[]> calculateRiskTrendsByMonthForAllSchools(@Param("startDate") java.time.LocalDate startDate,
                                                                      @Param("endDate") java.time.LocalDate endDate);
    
    // Calculate risk trends grouped by month for specific schools
    @Query(value = """
        SELECT 
            TO_CHAR(latest.created_at, 'Mon') as month,
            COUNT(CASE WHEN latest.risk_level = 'LOW' THEN 1 END) as low_risk,
            COUNT(CASE WHEN latest.risk_level = 'MEDIUM' THEN 1 END) as medium_risk,
            COUNT(CASE WHEN latest.risk_level = 'HIGH' THEN 1 END) as high_risk,
            COUNT(CASE WHEN latest.risk_level = 'CRITICAL' THEN 1 END) as critical_risk
        FROM (
            SELECT DISTINCT ON (dp.student_id)
                dp.student_id,
                dp.created_at,
                dp.risk_level
            FROM dropout_predictions dp
            JOIN student s ON dp.student_id = s.id
            WHERE dp.created_at >= :startDate AND dp.created_at <= :endDate
            AND s.school_id IN :schoolIds
            ORDER BY dp.student_id, dp.created_at DESC
        ) latest
        GROUP BY TO_CHAR(latest.created_at, 'Mon'), EXTRACT(MONTH FROM latest.created_at)
        ORDER BY EXTRACT(MONTH FROM latest.created_at)
        """, nativeQuery = true)
    java.util.List<Object[]> calculateRiskTrendsByMonthForSpecificSchools(@Param("startDate") java.time.LocalDate startDate,
                                                                           @Param("endDate") java.time.LocalDate endDate,
                                                                           @Param("schoolIds") java.util.List<UUID> schoolIds);
}
