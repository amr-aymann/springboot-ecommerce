package com.AmrShop.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {
    private final Key key;
    private final long jwtExpirationMs;

    public JwtUtil(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-ms}") long expMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = expMs;
    }

    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
          .setSubject(username)
          .claim("roles", roles)
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();
    }

    public Jws<Claims> validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
    }
}

