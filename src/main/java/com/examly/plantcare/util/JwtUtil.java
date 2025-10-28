package com.examly.plantcare.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessExpiryMs;
    private final long refreshExpiryMs;

    public JwtUtil(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.access.expiration-ms:3600000}") long accessExpiryMs,         // 1h
            @Value("${jwt.refresh.expiration-ms:604800000}") long refreshExpiryMs     // 7d
    ) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.accessExpiryMs = accessExpiryMs;
        this.refreshExpiryMs = refreshExpiryMs;
    }

    public String generateToken(String username, String role) {
        return buildToken(username, role, accessExpiryMs);
    }

    // Include role in refresh token too (your AuthService reads role from it)
    public String generateRefreshToken(String username, String role) {
        return buildToken(username, role, refreshExpiryMs);
    }

    private String buildToken(String username, String role, long ttlMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("role", role))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        Object r = getAllClaims(token).get("role");
        return r == null ? null : r.toString();
    }

    public boolean isTokenValid(String token) {
        try {
            getAllClaims(token); // parses & validates signature + exp
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private <T> T getClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(getAllClaims(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
