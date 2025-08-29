package com.AmrShop.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    // move these to application.properties later if you want
    private final String secretBase64 =
            "dGhpc19pc19hX3NhZmVfZGVtb19zZWNyZXRfdGhhdF9pcy0zMi1ieXRlcy1vcg==";
    private final long accessTtlSeconds = 60 * 60;       // 1h
    private final long refreshTtlSeconds = 60L * 60 * 24 * 7; // 7d

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
    }

    public String generateAccessToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setExpiration(Date.from(Instant.now().plusSeconds(accessTtlSeconds)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
                
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshTtlSeconds)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
                
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
