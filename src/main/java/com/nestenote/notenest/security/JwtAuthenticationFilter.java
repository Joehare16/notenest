package com.nestenote.notenest.security;

import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.nestenote.notenest.service.CustomUserDetailsService;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter)
    {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;

        //check if the JWT header exists
        if(authHeader != null || authHeader.startsWith("Bearer "))
        {
            //extract the token
           jwt = authHeader.substring(7);
           userEmail = jwtUtil.extractEmail(jwt);
        }
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            //get user details from db so we can work out permission through role
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
            if(jwtUtil.validateToken(jwt, userDetails.getUsername()))
            {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }
}
