package com.brightway.brightway_dropout.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    
    public UUID getCurrentUserId() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                log.warn("No HTTP request found in context");
                return null;
            }

            String token = extractTokenFromRequest(request);
            if (token == null) {
                log.warn("No JWT token found in request");
                return null;
            }

            Claims claims = parseToken(token);
            Object userIdClaim = claims.get("userId");

            if (userIdClaim instanceof UUID) {
                return (UUID) userIdClaim;
            } else if (userIdClaim instanceof String) {
                try {
                    return UUID.fromString((String) userIdClaim);
                } catch (IllegalArgumentException e) {
                    log.error("Error parsing userId as UUID: {}", e.getMessage());
                    return null;
                }
            }

            log.warn("UserId claim not found or invalid format in JWT token");
            return null;

        } catch (Exception e) {
            log.error("Error extracting user ID from JWT token: {}", e.getMessage());
            return null;
        }
    }

   
    public boolean isValidToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    
    public String getRoleFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Error extracting role from JWT token: {}", e.getMessage());
            return null;
        }
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
