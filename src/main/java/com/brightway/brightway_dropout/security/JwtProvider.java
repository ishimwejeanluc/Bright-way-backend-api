package com.brightway.brightway_dropout.security;

import com.brightway.brightway_dropout.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;


public class JwtProvider {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 1 day in ms

    public static String generateToken(User user) {
        Map<String, Object> claims = Map.of(
                "userId", user.getId() ,
                "email", user.getEmail() ,
                "role", user.getRole()
        );

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}


