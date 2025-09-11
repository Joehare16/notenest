package com.nestenote.notenest.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final String SECRET = "SUPER-SUPER-SUPER-SUPER-SUPER-SUPER-SECRET-KEY";

    //password for valdating and creating tokens
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    //each token lasts an hour
    private final long EXPIRATION = 1000 * 60 * 60;

    public String generateToken(Long userId, String email, String role)
    {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .addClaims(Map.of("email", email, "role", role))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(SECRET_KEY)
            .compact();
    }

    public Long extractUserId(String token)
    {
        return Long.valueOf(getClaims(token).getSubject());
    }
    public String extractEmail(String token)
    {
        return String.valueOf(getClaims(token).get("email"));
    }
     public String extractRole(String token)
    {
        return String.valueOf(getClaims(token).get("role"));
    }
    public Claims getClaims(String token)
    {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody(); 
    }
    public boolean validateToken(String token, Long userId)
    {
        Long tokenUserId = extractUserId(token);
        return(userId.equals(tokenUserId) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }
}
