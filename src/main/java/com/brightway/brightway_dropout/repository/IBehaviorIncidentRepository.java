   
package com.brightway.brightway_dropout.repository;

import com.brightway.brightway_dropout.model.BehaviorIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IBehaviorIncidentRepository extends JpaRepository<BehaviorIncident, UUID> {
    
    List<BehaviorIncident> findByStudentIdIn(Set<UUID> studentIds);
    
    @Query("SELECT bi FROM BehaviorIncident bi " +
           "JOIN bi.student s " +
           "JOIN Enrollment e ON e.student.id = s.id " +
           "JOIN e.course c " +
           "JOIN c.teacher t " +
           "WHERE t.id = :teacherId")
    List<BehaviorIncident> findByTeacherId(@Param("teacherId") UUID teacherId);

    @Query("SELECT COUNT(bi) FROM BehaviorIncident bi WHERE bi.student.id = :studentId")
    Integer countByStudentId(@Param("studentId") UUID studentId);
}
