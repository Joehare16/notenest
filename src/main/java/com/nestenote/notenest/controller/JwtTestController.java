package com.nestenote.notenest.controller;

import com.nestenote.notenest.security.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtTestController{

    private final JwtUtil jwtUtil;

    public JwtTestController(JwtUtil jwtUtil)
    {
        this.jwtUtil = jwtUtil;
    }
      // Generate a token for a given email
    @GetMapping("/test/generate")
    public String generateToken(@RequestParam String email) {
        return jwtUtil.generateToken(email);
    }

    // Validate a token against a given email
    @GetMapping("/test/validate")
    public boolean validateToken(@RequestParam String token, @RequestParam String email) {
        return jwtUtil.validateToken(token, email);
    }

    // Extract email from token
    @GetMapping("/test/extract")
    public String extractEmail(@RequestParam String token) {
        return jwtUtil.extractEmail(token);
    }
}