package com.AmrShop.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.expiration}") private long expirationMs;

    public String generateToken(UserDetails ud) {
        Map<String,Object> claims = new HashMap<>();
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(ud.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build()
            .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return Objects.nullSafeEquals(username, userDetails.getUsername());
        } catch (JwtException e) { return false; }
    }
}


