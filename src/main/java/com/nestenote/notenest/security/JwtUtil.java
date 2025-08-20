package com.nestenote.notenest.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    //password for valdating and creating tokens
    private final String SECRET_KEY = "secretKey";
    //each token lasts an hour
    private final long EXPIRATION = 1000 * 60 * 60;

    public String generateToken(String email)
    {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
    public String extractEmail(String token)
    {
        return Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();    
    }
    public boolean validateToken(String token, String userEmail)
    {
        String email = extractEmail(token);
        return(email.equals(userEmail) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        Date expiration = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return expiration.before(new Date());
    }
}
