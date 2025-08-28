package com.nestenote.notenest.controller;

import com.nestenote.notenest.dto.AuthRequest;
import com.nestenote.notenest.exception.ValidationException;
import com.nestenote.notenest.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.nestenote.notenest.security.JwtUtil;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    //handles authentication 
    private final AuthenticationManager authenticationManager;
    //handles jwt tokens 
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil)
    {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody AuthRequest authRequest)
    {
        System.out.println("[Login Attempt] Email: " + authRequest.getEmail());
        try{
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(), authRequest.getPassword()
                    )
            );
            System.out.println("[Login Success] Authentication passed");
            String token = jwtUtil.generateToken(authRequest.getEmail());
            System.out.println("[JWT Generated] Token length: " + token.length());
            return ResponseEntity.ok(Map.of("token",token));

        }catch (BadCredentialsException e)
        {
            throw new ValidationException("Invalid email or Password");
        } catch (UsernameNotFoundException e) {
        
            throw new ValidationException("User not found");
        } catch (Exception e) {
        throw new ValidationException("Authentication failed: " + e.getMessage());
    }
}
}

