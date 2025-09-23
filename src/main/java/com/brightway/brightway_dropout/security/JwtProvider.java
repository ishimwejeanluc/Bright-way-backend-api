package com.brightway.brightway_dropout.security;

import com.brightway.brightway_dropout.model.User;
import com.brightway.brightway_dropout.model.School;
import com.brightway.brightway_dropout.repository.ISchoolRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    private static final long EXPIRATION_TIME = 86400000*7; // 7 days in ms

    private final ISchoolRepository schoolRepository;

    public JwtProvider(ISchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("name", user.getName());

        // Add schoolId and schoolName for principal role
        if (user.getRole() != null && user.getRole().toString().equalsIgnoreCase("PRINCIPAL")) {
            schoolRepository.findByPrincipal_Id(user.getId())
                .ifPresent(school -> {
                    claims.put("schoolId", school.getId());
                    claims.put("schoolName", school.getName());
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


