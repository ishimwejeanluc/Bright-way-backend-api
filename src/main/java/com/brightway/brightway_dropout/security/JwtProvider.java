package com.brightway.brightway_dropout.security;

import com.brightway.brightway_dropout.model.User;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import com.brightway.brightway_dropout.repository.ITeacherRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Component
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    private static final long EXPIRATION_TIME = 86400000*7; // 7 days in ms

    private final ISchoolRepository schoolRepository;
    private final ITeacherRepository teacherRepository;
    private final IStudentRepository studentRepository;

    public JwtProvider(ISchoolRepository schoolRepository, ITeacherRepository teacherRepository, IStudentRepository studentRepository) {
        this.schoolRepository = schoolRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("name", user.getName());

        // Add schoolId and schoolName for principal role
        if (user.getRole() != null && user.getRole().toString().equalsIgnoreCase("PRINCIPAL")) {
            schoolRepository.findByPrincipalId(user.getId())
                .ifPresent(school -> {
                    claims.put("schoolId", school.getId());
                    claims.put("schoolName", school.getName());
                });
        }
        
        // Add teacher-specific claims for teacher role
        if (user.getRole() != null && user.getRole().toString().equalsIgnoreCase("TEACHER")) {
            teacherRepository.findByUserIdWithCourses(user.getId())
                .ifPresent(teacher -> {
                    // Add school info
                    if (teacher.getSchool() != null) {
                        claims.put("schoolId", teacher.getSchool().getId());
                        claims.put("schoolName", teacher.getSchool().getName());
                    }
                    
                    // Add courses array
                    if (teacher.getCourses() != null) {
                        List<Map<String, Object>> courses = teacher.getCourses().stream()
                            .map(course -> {
                                Map<String, Object> courseInfo = new HashMap<>();
                                courseInfo.put("courseId", course.getId().toString());
                                courseInfo.put("courseName", course.getName());
                                return courseInfo;
                            })
                            .toList();
                        claims.put("courses", courses);
                    }
                });
        }

        // Add student-specific claims for student role
        if (user.getRole() != null && user.getRole().toString().equalsIgnoreCase("STUDENT")) {
            studentRepository.findByUserIdWithSchool(user.getId())
                .ifPresent(student -> {
                    // Add school info
                    if (student.getSchool() != null) {
                        claims.put("schoolId", student.getSchool().getId());
                        claims.put("schoolName", student.getSchool().getName());
                    }
                    // Add student ID
                    claims.put("studentId", student.getId());
                });
        }

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }
}


